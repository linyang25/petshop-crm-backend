package com.petshop.crmbackend.controller;


import com.petshop.crmbackend.dto.DashboardDto;
import com.petshop.crmbackend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired private DashboardService dashboardService;

    /**
     * GET /api/dashboard
     * 返回前端仪表盘所需的所有聚合数据
     */
    @GetMapping
    public com.petshop.crmbackend.dto.DashboardDto getDashboard() {
        return dashboardService.getDashboard();
    }

}


//import com.petshop.crmbackend.common.ApiResponse;
//import com.petshop.crmbackend.dto.AppointmentStatsDto;
//import com.petshop.crmbackend.dto.PetStatsDto;
//import com.petshop.crmbackend.service.AppointmentStatsService;
//import com.petshop.crmbackend.service.PetStatsService;
//import io.swagger.v3.oas.annotations.Operation;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/stats")
//public class StatsController {
//
//    private final AppointmentStatsService appointmentStatsService;
//    private final PetStatsService petStatsService;
//
//    public StatsController(AppointmentStatsService appointmentStatsService,
//                           PetStatsService petStatsService) {
//        this.appointmentStatsService = appointmentStatsService;
//        this.petStatsService = petStatsService;
//    }
//
//    @GetMapping("/appointments")
//    @Operation(summary = "获取预约维度统计报表")
//    public ApiResponse<AppointmentStatsDto> getAppointmentStats() {
//        AppointmentStatsDto dto = appointmentStatsService.buildAppointmentStats();
//        return ApiResponse.success("Appointment statistics data", dto);
//    }
//
//    @GetMapping("/pets")
//    @Operation(summary = "获取宠物维度统计报表")
//    public ApiResponse<PetStatsDto> getPetStats() {
//        PetStatsDto dto = petStatsService.getPetStats();
//        return ApiResponse.success("Pet statistics data.", dto);
//    }
//}