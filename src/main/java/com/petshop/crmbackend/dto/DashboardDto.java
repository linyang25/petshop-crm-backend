package com.petshop.crmbackend.dto;

import java.util.List;

// 仪表盘整体数据 DTO
public class DashboardDto {
    private long totalPets;
    private long totalCustomers;
    private long appointmentsToday;
    private long cancelledToday;

    private List<LabelValue> speciesDistribution;
    private List<LabelValue> breedDistribution;
    private List<LabelValue> popularServices;
    private List<DayCount> weeklyAppointmentTrend;

    private List<AppointmentSummaryDto> todayAppointments;

    public long getTotalPets() { return totalPets; }
    public void setTotalPets(long totalPets) { this.totalPets = totalPets; }
    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }
    public long getAppointmentsToday() { return appointmentsToday; }
    public void setAppointmentsToday(long appointmentsToday) { this.appointmentsToday = appointmentsToday; }
    public long getCancelledToday() { return cancelledToday; }
    public void setCancelledToday(long cancelledToday) { this.cancelledToday = cancelledToday; }

    public List<LabelValue> getSpeciesDistribution() { return speciesDistribution; }
    public void setSpeciesDistribution(List<LabelValue> speciesDistribution) { this.speciesDistribution = speciesDistribution; }
    public List<LabelValue> getBreedDistribution() { return breedDistribution; }
    public void setBreedDistribution(List<LabelValue> breedDistribution) { this.breedDistribution = breedDistribution; }
    public List<LabelValue> getPopularServices() { return popularServices; }
    public void setPopularServices(List<LabelValue> popularServices) { this.popularServices = popularServices; }
    public List<DayCount> getWeeklyAppointmentTrend() { return weeklyAppointmentTrend; }
    public void setWeeklyAppointmentTrend(List<DayCount> weeklyAppointmentTrend) { this.weeklyAppointmentTrend = weeklyAppointmentTrend; }

    public List<AppointmentSummaryDto> getTodayAppointments() { return todayAppointments; }
    public void setTodayAppointments(List<AppointmentSummaryDto> todayAppointments) { this.todayAppointments = todayAppointments; }
}