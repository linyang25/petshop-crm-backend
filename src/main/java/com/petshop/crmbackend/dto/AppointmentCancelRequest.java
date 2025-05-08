package com.petshop.crmbackend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentCancelRequest {
    // 二选一：
    private String appointmentId;

    // 或者
    private Long petId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;

    // getters & setters
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public LocalTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalTime appointmentTime) { this.appointmentTime = appointmentTime; }
}