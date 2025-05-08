package com.petshop.crmbackend.service;

import com.petshop.crmbackend.dto.AppointmentStatsDto;
import com.petshop.crmbackend.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentStatsService {

    private final AppointmentRepository repo;

    public AppointmentStatsService(AppointmentRepository repo) {
        this.repo = repo;
    }

    public AppointmentStatsDto buildAppointmentStats() {
        AppointmentStatsDto dto = new AppointmentStatsDto();

        // **基础**
        dto.setTotalAppointments(repo.countTotal());
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
        dto.setTodayNewAppointments(repo.countTodayNew(todayStart));
        dto.setCancelledAppointments(repo.countCancelled());
        dto.setNoShowAppointments(repo.countNoShow());

        // **比率 & 平均**
        if (dto.getTotalAppointments() > 0) {
            dto.setCancellationRate((double) dto.getCancelledAppointments() / dto.getTotalAppointments());
            dto.setNoShowRate((double) dto.getNoShowAppointments() / dto.getTotalAppointments());
        } else {
            dto.setCancellationRate(0);
            dto.setNoShowRate(0);
        }
        Double avgLead = repo.avgLeadTimeDays();
        dto.setAvgLeadTimeDays(avgLead != null ? avgLead : 0.0);

        // **时段分布**
        // groupByTimeSlot() 返回 List<Object[]>，每行 [ "HH:00", count ]
        Map<String, Long> slotMap = repo.groupByTimeSlot().stream()
                .collect(Collectors.toMap(
                        row -> formatTimeSlot((String) row[0]),
                        row -> ((Number) row[1]).longValue(),
                        Long::sum,
                        TreeMap::new
                ));
        dto.setTimeSlotDistribution(slotMap);

        // **热门服务**
        List<AppointmentStatsDto.ServiceCount> top = repo.groupByService().stream()
                .map(row -> {
                    AppointmentStatsDto.ServiceCount sc = new AppointmentStatsDto.ServiceCount();
                    sc.setServiceType((String) row[0]);
                    sc.setCount(((Number) row[1]).longValue());
                    return sc;
                })
                .collect(Collectors.toList());
        dto.setTopServices(top);

        return dto;
    }

    /**
     * 将数据库中的 "HH:00" 转为 "HH:00-HH+1:00"，并且处理 23:00→00:00
     */
    private String formatTimeSlot(String hourKey) {
        // hourKey e.g. "14:00"
        int idx = hourKey.indexOf(':');
        int hour = Integer.parseInt(hourKey.substring(0, idx));
        int nextHour = (hour + 1) % 24;
        return String.format("%02d:00-%02d:00", hour, nextHour);
    }
}