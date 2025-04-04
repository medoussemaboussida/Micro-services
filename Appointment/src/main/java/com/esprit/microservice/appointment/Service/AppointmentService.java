package com.esprit.microservice.appointment.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.esprit.microservice.appointment.Entity.Appointment;
import com.esprit.microservice.appointment.Repository.AppointmentRepository;

import java.util.List;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    // ajout
    public Appointment addAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    // update
    public Appointment updateAppointment(int id, Appointment newAppointment) {
        if (appointmentRepository.findById(id).isPresent()) {
            Appointment existingAppointment = appointmentRepository.findById(id).get();
            existingAppointment.setPsychiatrist(newAppointment.getPsychiatrist());
            existingAppointment.setStudent(newAppointment.getStudent());
            existingAppointment.setDate(newAppointment.getDate());
            existingAppointment.setStartTime(newAppointment.getStartTime());
            existingAppointment.setEndTime(newAppointment.getEndTime());
            existingAppointment.setStatus(newAppointment.getStatus());

            return appointmentRepository.save(existingAppointment);
        } else {
            return null;
        }
    }

    // delete
    public String deleteAppointment(int id) {
        if (appointmentRepository.findById(id).isPresent()) {
            appointmentRepository.deleteById(id);
            return "appointment supprimé";
        } else {
            return "appointment non supprimé";
        }
    }

    // get
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // get by psychiatrist
    public List<Appointment> getByPsychiatrist(String psychiatrist) {
        return appointmentRepository.findByPsychiatrist(psychiatrist);
    }

    // get by id
    public Appointment getAppointmentById(int id) {
        return appointmentRepository.findById(id).orElse(null);
    }
}
