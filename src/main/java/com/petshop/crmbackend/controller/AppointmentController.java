package com.petshop.crmbackend.controller;

import com.petshop.crmbackend.common.ApiResponse;
import com.petshop.crmbackend.dto.AppointmentCancelRequest;
import com.petshop.crmbackend.dto.AppointmentRequest;
import com.petshop.crmbackend.entity.Appointment;
import com.petshop.crmbackend.entity.Pet;
import com.petshop.crmbackend.repository.AppointmentRepository;
import com.petshop.crmbackend.repository.PetRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;

    public AppointmentController(AppointmentRepository appointmentRepository,
                                 PetRepository petRepository) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
    }

    @PostMapping("/create")
    @Operation(summary = "创建预约")
    public ApiResponse<?> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        if (!petRepository.findByPetIdAndIsDeletedFalse(request.getPetId()).isPresent()) {
            return ApiResponse.error(400, "宠物ID无效或已被删除");
        }

        boolean exists = appointmentRepository.existsByPetIdAndAppointmentDateAndAppointmentTime(
                request.getPetId(),
                request.getAppointmentDate(),
                request.getAppointmentTime()
        );

        Appointment appointment = new Appointment();


        if (exists) {
            return ApiResponse.error(400, "该宠物在该时间已有预约");
        }

       // Appointment appointment = new Appointment();
        String code;
        do {
            code = String.format("%08d", new Random().nextInt(100_000_000));
        } while (appointmentRepository.existsByAppointmentId(code));


        appointment.setAppointmentId(code);
        appointment.setAppointmentTime(request.getAppointmentTime()); // 类型为 LocalTime
        appointment.setAppointmentDate(request.getAppointmentDate()); // 类型为 LocalDate
        appointment.setCustomerName(request.getCustomerName());
        appointment.setPetId(request.getPetId());
        appointment.setPhone(request.getPhone());
        appointment.setServiceType(request.getServiceType());
        appointment.setNotes(request.getNotes());
        appointment.setStatus("已预约");
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());

        appointmentRepository.save(appointment);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("petId", appointment.getPetId());
        responseData.put("appointmentId", appointment.getAppointmentId());
        responseData.put("appointmentDate", appointment.getAppointmentDate());
        responseData.put("appointmentTime", appointment.getAppointmentTime());

        return ApiResponse.success("预约创建成功", responseData);
    }

    @PutMapping("/update/{appointmentId}")
    @Operation(summary = "更新预约信息（按业务预约号查询，只能修改日期、时间、类型、状态、备注）")
    public ApiResponse<?> updateAppointment(
            @PathVariable String appointmentId,
            @Valid @RequestBody AppointmentRequest request) {

        Optional<Appointment> optional = appointmentRepository.findByAppointmentId(appointmentId);
        if (!optional.isPresent()) {
            return ApiResponse.error(404, "未找到对应的预约记录");
        }
        Appointment appointment = optional.get();

        // 只允许修改的字段
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setServiceType(request.getServiceType());
        appointment.setStatus(request.getStatus());
        appointment.setNotes(request.getNotes());
        appointment.setUpdatedAt(LocalDateTime.now());

        appointmentRepository.save(appointment);

        Map<String, Object> data = new HashMap<>();
        data.put("appointmentId", appointment.getAppointmentId());
        data.put("appointmentDate", appointment.getAppointmentDate());
        data.put("appointmentTime", appointment.getAppointmentTime());
        data.put("serviceType", appointment.getServiceType());
        data.put("status", appointment.getStatus());
        data.put("notes", appointment.getNotes());
        return ApiResponse.success("预约更新成功", data);
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
            return ApiResponse.error(400, "请提供 appointmentId，或 petId + appointmentDate + appointmentTime");
        }

        if (!optional.isPresent()) {
            return ApiResponse.error(404, "未找到对应的预约");
        }

        Appointment appointment = optional.get();
        if ("已取消".equals(appointment.getStatus())) {
            return ApiResponse.error(400, "该预约已是取消状态");
        }

        appointment.setStatus("已取消");
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        Map<String, Object> data = new HashMap<>();
        data.put("appointmentId", appointment.getAppointmentId());
        data.put("status", appointment.getStatus());
        return ApiResponse.success("预约取消成功", data);
    }
    @GetMapping("/detail/{appointmentId}")
    @Operation(summary = "获取预约详情")
    public ApiResponse<?> getAppointmentDetail(@PathVariable String appointmentId) {
        Optional<Appointment> optional = appointmentRepository.findByAppointmentId(appointmentId);
        if (!optional.isPresent()) {
            return ApiResponse.error(404, "未找到对应的预约");
        }
        Appointment ap = optional.get();
        // 再查宠物
        Optional<Pet> optionalPet = petRepository.findByPetIdAndIsDeletedFalse(ap.getPetId());
        if (!optionalPet.isPresent()) {
            return ApiResponse.error(404, "未找到对应的宠物");
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

        return ApiResponse.success("查询成功", data);
    }
}
