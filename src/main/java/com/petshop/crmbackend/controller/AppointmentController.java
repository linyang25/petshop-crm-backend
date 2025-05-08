package com.petshop.crmbackend.controller;

import com.petshop.crmbackend.common.ApiResponse;
import com.petshop.crmbackend.dto.AppointmentRequest;
import com.petshop.crmbackend.entity.Appointment;
import com.petshop.crmbackend.repository.AppointmentRepository;
import com.petshop.crmbackend.repository.PetRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

        if (exists) {
            return ApiResponse.error(400, "该宠物在此时间已有预约，不能重复预约");
        }

        Appointment appointment = new Appointment();
        appointment.setAppointmentTime(request.getAppointmentTime()); // 类型为 LocalTime
        appointment.setAppointmentDate(request.getAppointmentDate()); // 类型为 LocalDate
        appointment.setCustomerName(request.getCustomerName());
        appointment.setPetId(request.getPetId());
        appointment.setPhone(request.getPhone());
        appointment.setServiceType(request.getServiceType());
        appointment.setNotes(request.getNotes());
        appointment.setStatus("未开始");
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());

        appointmentRepository.save(appointment);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("petId", appointment.getPetId());
        responseData.put("appointmentDate", appointment.getAppointmentDate());
        responseData.put("appointmentTime", appointment.getAppointmentTime());

        return ApiResponse.success("预约创建成功", responseData);
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "更新预约信息（不可修改宠物ID）")
    public ApiResponse<?> updateAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentRequest request) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);
        if (!optionalAppointment.isPresent()) {
            return ApiResponse.error(404, "未找到对应的预约记录");
        }

        Appointment appointment = optionalAppointment.get();

        // 检查是否试图修改 petId（不允许修改）
        if (!appointment.getPetId().equals(request.getPetId())) {
            return ApiResponse.error(400, "不可修改宠物ID");
        }

        // 检查是否存在相同时间的重复预约
        boolean exists = appointmentRepository.existsByPetIdAndAppointmentDateAndAppointmentTimeAndIdNot(
                request.getPetId(), request.getAppointmentDate(), request.getAppointmentTime(), id
        );
        if (exists) {
            return ApiResponse.error(400, "该宠物在该时间已有预约，无法修改");
        }

        // 更新可修改字段
        appointment.setCustomerName(request.getCustomerName());
        appointment.setPhone(request.getPhone());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setServiceType(request.getServiceType());
        appointment.setStatus(request.getStatus());
        appointment.setNotes(request.getNotes());
        appointment.setUpdatedAt(LocalDateTime.now());

        appointmentRepository.save(appointment);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("appointmentId", appointment.getId());
        return ApiResponse.success("预约信息更新成功", responseData);
    }
}