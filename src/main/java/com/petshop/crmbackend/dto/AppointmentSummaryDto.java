package com.petshop.crmbackend.dto;

// 当日预约简略信息，用于表格
public class AppointmentSummaryDto {
    private String time;           // "09:00 AM"
    private String customerName;   // 客户名
    private String petName;        // 宠物名
    private String service;        // 服务类型
    private String status;         // Scheduled / Cancelled
    private boolean reminderSent;  // true 已发送，false 未发送

    // ======= constructors =======
    public AppointmentSummaryDto() {}

    // ======= getters & setters =======
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getService() { return service; }
    public void setService(String service) { this.service = service; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isReminderSent() { return reminderSent; }
    public void setReminderSent(boolean reminderSent) { this.reminderSent = reminderSent; }
}
