package com.petshop.crmbackend.dto;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentRequest {

    @NotNull(message = "宠物ID不能为空")
    private Long petId;

    @NotNull(message = "客户姓名不能为空")
    private String customerName;

    @NotNull(message = "联系电话不能为空")
    private String phone;

    @NotNull(message = "客户邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String customerEmail;

    @NotNull(message = "预约日期不能为空")
    private LocalDate appointmentDate;

    @NotNull(message = "预约时间不能为空")
    private LocalTime appointmentTime;

    @NotNull(message = "服务类型不能为空")
    private String serviceType;

    private String status;     // 可选
    private String notes;      // 可选

    // --- Getter 和 Setter ---
    public Long getPetId() {
        return petId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }


    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}