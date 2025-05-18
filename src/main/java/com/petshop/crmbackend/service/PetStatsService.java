//// com.petshop.crmbackend.service.PetStatsService.java
//package com.petshop.crmbackend.service;
//
//import com.petshop.crmbackend.dto.PetStatsDto;
//import com.petshop.crmbackend.repository.PetRepository;
//import org.springframework.stereotype.Service;
//
//import java.time.*;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class PetStatsService {
//
//    private final PetRepository petRepo;
//
//    public PetStatsService(PetRepository petRepo) {
//        this.petRepo = petRepo;
//    }
//
//    public PetStatsDto getPetStats() {
//        PetStatsDto dto = new PetStatsDto();
//
//        LocalDateTime now = LocalDateTime.now();
//        // 今日区间
//        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
//        LocalDateTime tomorrowStart = todayStart.plusDays(1);
//        // 本周（周一开始）
//        LocalDate weekStartDate = now.toLocalDate().with(DayOfWeek.MONDAY);
//        LocalDateTime weekStart = weekStartDate.atStartOfDay();
//        // 本月
//        LocalDate monthStartDate = now.toLocalDate().withDayOfMonth(1);
//        LocalDateTime monthStart = monthStartDate.atStartOfDay();
//
//        dto.setTotalPets(petRepo.countByIsDeletedFalse());
//        dto.setTodayNewPets(petRepo.countByCreatedAtBetween(todayStart, tomorrowStart));
//        dto.setWeekNewPets(petRepo.countByCreatedAtBetween(weekStart, now));
//        dto.setMonthNewPets(petRepo.countByCreatedAtBetween(monthStart, now));
//
//        // 按物种分布
//        Map<String, Long> speciesMap = petRepo.countBySpecies().stream()
//                .collect(Collectors.toMap(
//                        arr -> (String)arr[0],
//                        arr -> (Long)arr[1]
//                ));
//        dto.setSpeciesDistribution(speciesMap);
//
//        // 按品种分布
//        Map<String, Long> breedMap = petRepo.countByBreed().stream()
//                .collect(Collectors.toMap(
//                        arr -> (String)arr[0],
//                        arr -> (Long)arr[1]
//                ));
//        dto.setBreedDistribution(breedMap);
//
//        // 平均年龄（天）
//        Double avg = petRepo.averageAgeDays();
//        dto.setAverageAgeDays(avg != null ? avg : 0);
//
//        return dto;
//    }
//}