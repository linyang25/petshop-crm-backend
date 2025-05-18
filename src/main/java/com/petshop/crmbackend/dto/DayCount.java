package com.petshop.crmbackend.dto;

// 用于折线图：每日预约数
public class DayCount {
    private String day;   // Mon、Tue … Sun
    private long count;

    public DayCount() {}
    public DayCount(String day, long count) {
        this.day = day;
        this.count = count;
    }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}