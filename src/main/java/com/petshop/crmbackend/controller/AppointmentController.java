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
}