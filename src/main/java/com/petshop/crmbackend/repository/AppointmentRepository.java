package com.petshop.crmbackend.repository;

import com.petshop.crmbackend.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByPetIdAndAppointmentDateAndAppointmentTime(Long petId, LocalDate appointmentDate, LocalTime appointmentTime);
}
