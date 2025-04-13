package com.esprit.microservice.appointment.Repository;

import com.esprit.microservice.appointment.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    List<Appointment> findByPsychiatrist(String psychiatrist);
}