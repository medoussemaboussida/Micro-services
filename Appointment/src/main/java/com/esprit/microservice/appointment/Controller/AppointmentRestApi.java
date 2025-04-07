package com.esprit.microservice.appointment.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.esprit.microservice.appointment.Entity.Appointment;
import com.esprit.microservice.appointment.Service.AppointmentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointment")
public class AppointmentRestApi {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        appointment.setStatus(Appointment.Status.PENDING);
        Appointment createdAppointment = appointmentService.addAppointment(appointment);
        return new ResponseEntity<>(createdAppointment, HttpStatus.OK);
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
    @PutMapping(value = "/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Appointment> updateAppointmentStatus(
            @PathVariable("id") int id,
            @RequestBody Map<String, String> statusUpdate) {
        String newStatusStr = statusUpdate.get("status");
        try {
            Appointment.Status newStatus = Appointment.Status.valueOf(newStatusStr.toUpperCase());
            Appointment updatedAppointment = appointmentService.updateStatus(id, newStatus);
            return new ResponseEntity<>(updatedAppointment, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Statut invalide
        }
    }

    @GetMapping(value = "/export-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportAppointmentsToPdf() {
        byte[] pdfContent = appointmentService.exportAppointmentsToPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=appointments.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }
    @GetMapping(value = "/export-csv", produces = "text/csv")
    public ResponseEntity<String> exportAppointmentsToCsv() {
        String csvContent = appointmentService.exportAppointmentsToCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=appointments.csv")
                .body(csvContent);
    }


    @GetMapping(value = "/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> getAppointmentStats() {
        Map<String, Long> stats = appointmentService.getAppointmentStats();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }
    //http://localhost:8056/appointment/appointments/filter-by-date?order=old
    @GetMapping("/filter-by-date")
    public ResponseEntity<List<Appointment>> getAppointmentsByDateOrder(
            @RequestParam(defaultValue = "recent") String order) {
        if (!order.equalsIgnoreCase("recent") && !order.equalsIgnoreCase("old")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Paramètre invalide
        }
        List<Appointment> appointments = appointmentService.getAppointmentsByDateOrder(order);
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }
}