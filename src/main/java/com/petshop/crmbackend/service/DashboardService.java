package com.petshop.crmbackend.service;

import com.petshop.crmbackend.dto.AppointmentSummaryDto;
import com.petshop.crmbackend.dto.DashboardDto;
import com.petshop.crmbackend.dto.DayCount;
import com.petshop.crmbackend.dto.LabelValue;
import com.petshop.crmbackend.entity.Appointment;
import com.petshop.crmbackend.entity.Pet;
import com.petshop.crmbackend.repository.AppointmentRepository;
import com.petshop.crmbackend.repository.PetRepository;
import com.petshop.crmbackend.repository.projection.DayCountProjection;
import com.petshop.crmbackend.repository.projection.LabelValueProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("hh:mm a");

    @Autowired
    private PetRepository petRepo;

    @Autowired
    private AppointmentRepository apptRepo;

    public DashboardDto getDashboard() {
        LocalDate today = LocalDate.now();
        DashboardDto dto = new DashboardDto();

        // 1. 基本统计
        dto.setTotalPets(petRepo.countByIsDeletedFalse() );
        dto.setAppointmentsToday( apptRepo.countByAppointmentDate(today) );
        dto.setCancelledToday( apptRepo.countByAppointmentDateAndCancelled(today) );


        // —— 新增：今日新增宠物数 ——
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime startOfTomorrow = today.plusDays(1).atStartOfDay();
        long newPetsToday = petRepo.countByCreatedAtBetweenAndIsDeletedFalse(startOfToday, startOfTomorrow);
        dto.setNewPetsToday(newPetsToday);


        // 2. 物种分布（基于所有未删除的宠物，带百分比）--宠物信息
        List<LabelValueProjection> rawSpecies = petRepo.countBySpecies();
        long totalSpecies = rawSpecies.stream()
                .mapToLong(LabelValueProjection::getValue)
                .sum();
        List<LabelValue> speciesDist = rawSpecies.stream()
                .map(p -> {
                    long v = p.getValue();
                    double pct = totalSpecies > 0
                            ? Math.round(v * 10000.0 / totalSpecies) / 100.0
                            : 0.0;
                    return new LabelValue(p.getLabel(), v, pct);
                })
                .collect(Collectors.toList());
        dto.setSpeciesDistribution(speciesDist);


        // 3. 品种分布（基于所有未删除的宠物，带百分比）
        List<LabelValueProjection> rawBreed = petRepo.countByBreed();
        long totalBreed = rawBreed.stream()
                .mapToLong(LabelValueProjection::getValue)
                .sum();

        List<LabelValue> breedDist = rawBreed.stream()
                .map(p -> {
                    long v = p.getValue();
                    double pct = totalBreed > 0
                            ? Math.round(v * 10000.0 / totalBreed) / 100.0
                            : 0.0;
                    return new LabelValue(p.getLabel(), v, pct);
                })
                .collect(Collectors.toList());

        dto.setBreedDistribution(breedDist);


        // 4. 热门服务（带百分比）
        List<LabelValueProjection> rawSvc = apptRepo.countByServiceType();
        long totalSvc = rawSvc.stream()
                .mapToLong(LabelValueProjection::getValue).sum();
        List<LabelValue> popularSvc = rawSvc.stream()
                .map(p -> {
                    long v = p.getValue();
                    double pct = totalSvc > 0
                            ? Math.round(v * 10000.0 / totalSvc) / 100.0
                            : 0.0;
                    return new LabelValue(p.getLabel(), v, pct);
                })
                .collect(Collectors.toList());
        dto.setPopularServices(popularSvc);

        // 5. 最近7天预约趋势
        List<DayCountProjection> rawTrend = apptRepo.countPerDayLast7Days();
        Map<String, Long> trendMap = rawTrend.stream()
                .collect(Collectors.toMap(DayCountProjection::getDay, DayCountProjection::getCount));
        List<String> weekDays = Arrays.asList(
                "Monday","Tuesday","Wednesday",
                "Thursday","Friday","Saturday","Sunday"
        );
        List<DayCount> trend = weekDays.stream()
                .map(day -> new DayCount(day, trendMap.getOrDefault(day, 0L)))
                .collect(Collectors.toList());
        dto.setWeeklyAppointmentTrend(trend);

        // 6. 今日预约列表
        List<Appointment> todayAppts = apptRepo.findByAppointmentDate(today);
        Map<Long, String> petNameMap = petRepo.findAllByPetIdIn(
                todayAppts.stream().map(Appointment::getPetId).distinct().collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(Pet::getPetId, Pet::getPetName));
        List<AppointmentSummaryDto> todayList = todayAppts.stream()
                .map(a -> {
                    AppointmentSummaryDto s = new AppointmentSummaryDto();
                    s.setTime( a.getAppointmentTime().format(TIME_FMT) );
                    s.setCustomerName( a.getCustomerName() );
                    s.setPetName( petNameMap.getOrDefault(a.getPetId(), "") );
                    s.setService( a.getServiceType() );
                    s.setStatus( a.getStatus() );
                    s.setReminderSent( a.isReminderSent() );
                    return s;
                }).collect(Collectors.toList());
        dto.setTodayAppointments(todayList);

        return dto;
    }
}
