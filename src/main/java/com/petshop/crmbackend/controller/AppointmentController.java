package com.petshop.crmbackend.controller;


import javax.persistence.criteria.Predicate;

import com.petshop.crmbackend.config.DailyReminderScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petshop.crmbackend.common.ApiResponse;
import com.petshop.crmbackend.dto.AppointmentCancelRequest;
import com.petshop.crmbackend.dto.AppointmentRequest;
import com.petshop.crmbackend.entity.Appointment;
import com.petshop.crmbackend.entity.Pet;
import com.petshop.crmbackend.repository.AppointmentRepository;
import com.petshop.crmbackend.repository.PetRepository;
import com.petshop.crmbackend.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import com.petshop.crmbackend.config.DailyReminderScheduler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final EmailService emailService;
    private static final Logger log = LoggerFactory.getLogger(AppointmentController.class);
    private final DailyReminderScheduler scheduler;


    public AppointmentController(AppointmentRepository appointmentRepository,
                                 PetRepository petRepository,EmailService emailService,DailyReminderScheduler scheduler) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.emailService = emailService;
        this.scheduler = scheduler;
    }



    @GetMapping("/trigger-reminder")
    @Operation(summary = "定时任务测试接口")
    public String triggerReminder() {
        scheduler.sendTodayAppointmentsReminder();
        return "Daily reminder triggered";
    }


    @PostMapping("/create")
    @Operation(summary = "创建预约")
    public ApiResponse<?> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        // 1. 校验宠物
        if (!petRepository.findByPetIdAndIsDeletedFalse(request.getPetId()).isPresent()) {
            return ApiResponse.error(400, "The pet information was not found or has already been deleted.");
        }

        // 2. 去重
        boolean exists = appointmentRepository
                .existsByPetIdAndAppointmentDateAndAppointmentTime(
                        request.getPetId(),
                        request.getAppointmentDate(),
                        request.getAppointmentTime()
                );
        if (exists) {
            return ApiResponse.error(400, "The pet already has an appointment at that time");
        }

        // 3. 构造 Appointment
        Appointment appointment = new Appointment();
        String code;
        do {
            code = String.format("%08d", new Random().nextInt(100_000_000));
        } while (appointmentRepository.existsByAppointmentId(code));
        appointment.setAppointmentId(code);
        appointment.setPetId(request.getPetId());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setCustomerName(request.getCustomerName());
        appointment.setPhone(request.getPhone());
        appointment.setCustomerEmail(request.getCustomerEmail());
        appointment.setServiceType(request.getServiceType());
        appointment.setNotes(request.getNotes());
        appointment.setStatus("Scheduled");
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());

        // 4. 保存到数据库
        appointmentRepository.save(appointment);

        // 5. 准备邮件
        String to      = appointment.getCustomerEmail();
        String subject = String.format("[Pet Service Team] Your appointment is confirmed: %s", appointment.getAppointmentId());
        String body    = String.format(
                "Hello %s,\n\n" +
                        "Your appointment (ID: %s) has been successfully booked for %s at %s to receive \"%s\".\n\n" +
                        "Notes: %s\n\n" +
                        "Thank you for choosing our Pet Management System!\n",
                appointment.getCustomerName(),
                appointment.getAppointmentId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getServiceType(),
                appointment.getNotes()
        );

        // 6. 发送邮件（捕获异常，避免影响主流程）
        try {
            emailService.sendAppointmentReminder(to, subject, body);
        } catch (Exception e) {
            log.error("给 {} 发送预约确认邮件失败，预约ID={}", to, appointment.getAppointmentId(), e);
        }

        // 7. 构造返回
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("petId",           appointment.getPetId());
        responseData.put("appointmentId",   appointment.getAppointmentId());
        responseData.put("appointmentDate", appointment.getAppointmentDate());
        responseData.put("appointmentTime", appointment.getAppointmentTime());
        responseData.put("customerEmail",   appointment.getCustomerEmail());

        return ApiResponse.success("Appointment created successfully, and an email has been sent.", responseData);
    }

    @PutMapping("/update/{appointmentId}")
    @Operation(summary = "更新预约信息（按业务预约号查询，只能修改日期、时间、类型、状态、备注）")
    public ApiResponse<?> updateAppointment(
            @PathVariable String appointmentId,
            @Valid @RequestBody AppointmentRequest request) {

        Optional<Appointment> optional = appointmentRepository.findByAppointmentId(appointmentId);
        if (!optional.isPresent()) {
            return ApiResponse.error(404, "No matching appointment record was found.");
        }
        Appointment appointment = optional.get();

        // 只允许修改的字段
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setServiceType(request.getServiceType());
        appointment.setStatus(request.getStatus());
        appointment.setNotes(request.getNotes());
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment.setStatus("Rescheduled");


        appointmentRepository.save(appointment);


        // —— 新增：发送更新通知邮件 ——
        String to      = appointment.getCustomerEmail();
        String subject = String.format("[Pet Service Team] Your appointment has been updated: %s", appointment.getAppointmentId());
        String body    = String.format(
                "Hello %s,\n\n" +
                        "Your appointment (ID: %s) has been successfully updated for %s at %s to receive \"%s\".\n\n" +
                        "Notes: %s\n\n" +
                        "Thank you for choosing our Pet Management System!\n",
                appointment.getCustomerName(),
                appointment.getAppointmentId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getServiceType(),
                appointment.getStatus(),
                appointment.getNotes()
        );
        try {
            emailService.sendAppointmentReminder(to, subject, body);
        } catch (Exception e) {
            log.error("发送更新邮件失败，appointmentId={}", appointmentId, e);
        }


        Map<String, Object> data = new HashMap<>();
        data.put("appointmentId", appointment.getAppointmentId());
        data.put("appointmentDate", appointment.getAppointmentDate());
        data.put("appointmentTime", appointment.getAppointmentTime());
        data.put("serviceType", appointment.getServiceType());
        data.put("status", appointment.getStatus());
        data.put("notes", appointment.getNotes());
        //data.put("customerEmail", appointment.getCustomerEmail());

        return ApiResponse.success("Appointment updated successfully, and a confirmation email has been sent.", data);
    }


    @PutMapping("/cancel")
    @Operation(summary = "取消预约（根据 appointmentId，或 根据 petId+日期+时间）")
    public ApiResponse<?> cancelAppointment(@Valid @RequestBody AppointmentCancelRequest req) {
        Optional<Appointment> optional = Optional.empty();

        // 优先用 appointmentId
        if (req.getAppointmentId() != null) {
            optional = appointmentRepository.findByAppointmentId(req.getAppointmentId());

    } else if (req.getPetId() != null
                && req.getAppointmentDate() != null
                && req.getAppointmentTime() != null) {
            optional = appointmentRepository
                    .findByPetIdAndAppointmentDateAndAppointmentTime(
                            req.getPetId(),
                            req.getAppointmentDate(),
                            req.getAppointmentTime());
        } else {
            return ApiResponse.error(400, "Please provide the appointmentId, or the petId, appointmentDate, and appointmentTime.");
        }

        if (!optional.isPresent()) {
            return ApiResponse.error(404, "No matching appointment record was found.");
        }

        Appointment appointment = optional.get();
        if ("Cancelled".equals(appointment.getStatus())) {
            return ApiResponse.error(400, "This appointment has already been canceled.");
        }

        appointment.setStatus("Cancelled");
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        // —— 新增：发送取消确认邮件 ——
        String to      = appointment.getCustomerEmail();
        String subject = String.format("[Pet Service Team] Your appointment has been cancelled: %s", appointment.getAppointmentId());
        String body    = String.format(
                "Hello %s,\n\n" +
                        "We regret to inform you that your appointment (ID: %s) scheduled on %s at %s for “%s” has been cancelled.\n\n" +
                        "If you wish to reschedule, please feel free to contact us.\n\n" +
                        "Thank you for choosing our Pet Management System!\n",
                appointment.getCustomerName(),
                appointment.getAppointmentId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getServiceType()
        );
        try {
            emailService.sendAppointmentReminder(to, subject, body);
        } catch (Exception e) {
            log.error("Failed to send cancellation email to {}, appointmentId={}",
                    to, appointment.getAppointmentId(), e);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("appointmentId", appointment.getAppointmentId());
        data.put("status", appointment.getStatus());
        return ApiResponse.success("Appointment cancelled successfully and confirmation email sent.", data);
    }
    @GetMapping("/detail/{appointmentId}")
    @Operation(summary = "获取预约详情")
    public ApiResponse<?> getAppointmentDetail(@PathVariable String appointmentId) {
        Optional<Appointment> optional = appointmentRepository.findByAppointmentId(appointmentId);
        if (!optional.isPresent()) {
            return ApiResponse.error(404, "No matching appointment record was found.");
        }
        Appointment ap = optional.get();
        // 再查宠物
        Optional<Pet> optionalPet = petRepository.findByPetIdAndIsDeletedFalse(ap.getPetId());
        if (!optionalPet.isPresent()) {
            return ApiResponse.error(404, "No matching pet information was found.");
        }
        Pet pet = optionalPet.get();

        Map<String,Object> data = new HashMap<>();
        data.put("appointmentId",     ap.getAppointmentId());
        data.put("petId",             ap.getPetId());
        data.put("petName",         pet.getPetName());
        data.put("species",         pet.getSpecies());
        data.put("breedName",       pet.getBreedName());
        data.put("serviceType",       ap.getServiceType());
        data.put("appointmentDate",   ap.getAppointmentDate());
        data.put("appointmentTime",   ap.getAppointmentTime());
        data.put("notes",             ap.getNotes());
        data.put("customerName",      ap.getCustomerName());
        data.put("phone",             ap.getPhone());
        data.put("status",            ap.getStatus());
        data.put("createdAt",         ap.getCreatedAt());
        data.put("updatedAt",         ap.getUpdatedAt());
        data.put("customerEmail",         ap.getCustomerEmail());


        return ApiResponse.success("Query successful.", data);
    }


    @GetMapping("/list")
    @Operation(summary = "多条件查询预约列表（支持 petId, appointmentId, customerName, petName, status, 日期区间 等，无分页）")
    public ApiResponse<?> listAppointments(
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) String appointmentId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String petName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Specification<Appointment> spec = (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();

            if (petId != null) {
                preds.add(cb.equal(root.get("petId"), petId));
            }
            if (appointmentId != null && !appointmentId.isEmpty()) {
                preds.add(cb.equal(root.get("appointmentId"), appointmentId));
            }
            if (customerName != null && !customerName.isEmpty()) {
                preds.add(cb.like(root.get("customerName"), "%" + customerName + "%"));
            }
            if (status != null && !status.isEmpty()) {
                preds.add(cb.equal(root.get("status"), status));
            }
            if (startDate != null) {
                preds.add(cb.greaterThanOrEqualTo(root.get("appointmentDate"), startDate));
            }
            if (endDate != null) {
                preds.add(cb.lessThanOrEqualTo(root.get("appointmentDate"), endDate));
            }

            if (petName != null && !petName.isEmpty()) {
                // 先查出匹配的 petId 列表
                List<Long> ids = petRepository
                        .findByPetNameContaining(petName) // 或改用 findByPetNameContaining
                        .stream()
                        .map(Pet::getPetId)
                        .collect(Collectors.toList());
                if (ids.isEmpty()) {
                    preds.add(cb.disjunction()); // 无记录
                } else {
                    preds.add(root.get("petId").in(ids));
                }
            }

            return cb.and(preds.toArray(new Predicate[0]));
        };

        // 按日期、时间倒序
        Sort sort = Sort.by("appointmentDate").descending()
                .and(Sort.by("appointmentTime").descending());

        List<Appointment> resultList = appointmentRepository.findAll(spec, sort);

        List<Map<String,Object>> list = resultList.stream().map(ap -> {
            Map<String,Object> m = new HashMap<>();
            // 查一下宠物
            petRepository.findByPetIdAndIsDeletedFalse(ap.getPetId())
                    .ifPresent(pet -> {
                        m.put("petName", pet.getPetName());
                        m.put("species",  pet.getSpecies());
                        m.put("breed",    pet.getBreedName());
                    });
            m.put("appointmentId",   ap.getAppointmentId());
            m.put("petId",           ap.getPetId());
            m.put("customerName",    ap.getCustomerName());
            m.put("phone",           ap.getPhone());
            m.put("serviceType",     ap.getServiceType());
            m.put("status",          ap.getStatus());
            m.put("appointmentDate", ap.getAppointmentDate());
            m.put("appointmentTime", ap.getAppointmentTime());
            m.put("notes",           ap.getNotes());
            m.put("customerEmail",         ap.getCustomerEmail());
            return m;
        }).collect(Collectors.toList());

        return ApiResponse.success("Query successful.", list);
    }



}

