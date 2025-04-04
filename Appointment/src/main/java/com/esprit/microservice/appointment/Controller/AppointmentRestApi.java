package com.esprit.microservice.appointment.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.esprit.microservice.appointment.Entity.Appointment;
import com.esprit.microservice.appointment.Service.AppointmentService;

import java.util.List;

@RestController
@RequestMapping("/appointment")
public class AppointmentRestApi {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        return new ResponseEntity<>(appointmentService.addAppointment(appointment), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Appointment> updateAppointment(@PathVariable(value = "id") int id, @RequestBody Appointment appointment) {
        return new ResponseEntity<>(appointmentService.updateAppointment(id, appointment), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteAppointment(@PathVariable(value = "id") int id) {
        return new ResponseEntity<>(appointmentService.deleteAppointment(id), HttpStatus.OK);
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return new ResponseEntity<>(appointmentService.getAllAppointments(), HttpStatus.OK);
    }

    @GetMapping("/by-psychiatrist/{psychiatrist}")
    public ResponseEntity<List<Appointment>> getAppointmentsByPsychiatrist(@PathVariable("psychiatrist") String psychiatrist) {
        List<Appointment> appointments = appointmentService.getByPsychiatrist(psychiatrist);
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable("id") int id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment != null) {
            return new ResponseEntity<>(appointment, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}