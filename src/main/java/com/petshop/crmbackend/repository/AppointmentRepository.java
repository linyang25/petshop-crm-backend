package com.petshop.crmbackend.repository;

import com.petshop.crmbackend.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByPetIdAndAppointmentDateAndAppointmentTimeAndIdNot(
            Long petId,
            java.time.LocalDate appointmentDate,
            java.time.LocalTime appointmentTime,
            Long id
    );
    boolean existsByPetIdAndAppointmentDateAndAppointmentTime(Long petId, LocalDate appointmentDate, LocalTime appointmentTime);
    boolean existsByPetId(Long petId);

    Appointment findTopByPetIdOrderByAppointmentDateDescAppointmentTimeDesc(Long petId);
    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);
}

