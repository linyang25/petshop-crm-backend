package com.petshop.crmbackend.dto;

import java.util.List;
import java.util.Map;

public class AppointmentStatsDto {

    /** 累计预约数 */
    private long totalAppointments;
    /** 今日新增预约数 */
    private long todayNewAppointments;
    /** 已取消预约数 */
    private long cancelledAppointments;
    /** 未到访（爽约）预约数 */
    private long noShowAppointments;

    /** 取消率 = cancelledAppointments / totalAppointments */
    private double cancellationRate;
    /** 未到访率 = noShowAppointments / totalAppointments */
    private double noShowRate;
    /** 平均提前预约天数 */
    private double avgLeadTimeDays;

    /** 按小时段统计的预约分布，key="09:00-10:00"，value=数量 */
    private Map<String, Long> timeSlotDistribution;
    /** 最受欢迎的服务类型排行榜 */
    private List<ServiceCount> topServices;

    public AppointmentStatsDto() { }

    // --- getters & setters ---

    public long getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(long totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public long getTodayNewAppointments() {
        return todayNewAppointments;
    }

    public void setTodayNewAppointments(long todayNewAppointments) {
        this.todayNewAppointments = todayNewAppointments;
    }

    public long getCancelledAppointments() {
        return cancelledAppointments;
    }

    public void setCancelledAppointments(long cancelledAppointments) {
        this.cancelledAppointments = cancelledAppointments;
    }

    public long getNoShowAppointments() {
        return noShowAppointments;
    }

    public void setNoShowAppointments(long noShowAppointments) {
        this.noShowAppointments = noShowAppointments;
    }

    public double getCancellationRate() {
        return cancellationRate;
    }

    public void setCancellationRate(double cancellationRate) {
        this.cancellationRate = cancellationRate;
    }

    public double getNoShowRate() {
        return noShowRate;
    }

    public void setNoShowRate(double noShowRate) {
        this.noShowRate = noShowRate;
    }

    public double getAvgLeadTimeDays() {
        return avgLeadTimeDays;
    }

    public void setAvgLeadTimeDays(double avgLeadTimeDays) {
        this.avgLeadTimeDays = avgLeadTimeDays;
    }

    public Map<String, Long> getTimeSlotDistribution() {
        return timeSlotDistribution;
    }

    public void setTimeSlotDistribution(Map<String, Long> timeSlotDistribution) {
        this.timeSlotDistribution = timeSlotDistribution;
    }

    public List<ServiceCount> getTopServices() {
        return topServices;
    }

    public void setTopServices(List<ServiceCount> topServices) {
        this.topServices = topServices;
    }

    // --- 内部 DTO: 服务类型及其计数 ---
    public static class ServiceCount {
        private String serviceType;
        private long count;

        public ServiceCount() { }

        public ServiceCount(String serviceType, long count) {
            this.serviceType = serviceType;
            this.count = count;
        }

        public String getServiceType() {
            return serviceType;
        }

        public void setServiceType(String serviceType) {
            this.serviceType = serviceType;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }
}