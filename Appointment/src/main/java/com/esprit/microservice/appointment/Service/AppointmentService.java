package com.esprit.microservice.appointment.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.esprit.microservice.appointment.Entity.Appointment;
import com.esprit.microservice.appointment.Repository.AppointmentRepository;
import java.text.SimpleDateFormat;
import java.io.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    // ajout
    public Appointment addAppointment(Appointment appointment) {
        appointment.setStatus(Appointment.Status.PENDING); // Forcer PENDING
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




    public String exportAppointmentsToCsv() {
        List<Appointment> appointments = getAllAppointments();

        // Formatteur pour la date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // En-têtes du CSV
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("ID,Psychiatrist,Student,Date,Start Time,End Time,Status\n");

        // Ajouter les données des rendez-vous
        for (Appointment appointment : appointments) {
            csvBuilder.append(appointment.getId()).append(",");
            csvBuilder.append(escapeCsv(appointment.getPsychiatrist())).append(",");
            csvBuilder.append(escapeCsv(appointment.getStudent())).append(",");
            csvBuilder.append(dateFormat.format(appointment.getDate())).append(",");
            csvBuilder.append(escapeCsv(appointment.getStartTime())).append(",");
            csvBuilder.append(escapeCsv(appointment.getEndTime())).append(",");
            csvBuilder.append(appointment.getStatus()).append("\n");
        }

        return csvBuilder.toString();
    }

    // Méthode utilitaire pour échapper les virgules ou guillemets dans les champs
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        // Si le champ contient une virgule ou des guillemets, entourez-le de guillemets
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    // get by psychiatrist
    public List<Appointment> getByPsychiatrist(String psychiatrist) {
        return appointmentRepository.findByPsychiatrist(psychiatrist);
    }

    public Appointment updateStatus(int id, Appointment.Status newStatus) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        appointment.setStatus(newStatus);
        return appointmentRepository.save(appointment);
    }
//filter by date
    public List<Appointment> getAppointmentsByDateOrder(String order) {
        Sort sort = order.equalsIgnoreCase("recent")
                ? Sort.by(Sort.Direction.DESC, "date") // Plus récent en premier
                : Sort.by(Sort.Direction.ASC, "date"); // Plus ancien en premier
        return appointmentRepository.findAll(sort);
    }

// stat sur rendez vous
    public Map<String, Long> getAppointmentStats() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream()
                .collect(Collectors.groupingBy(
                        appointment -> appointment.getStatus().toString(),
                        Collectors.counting()
                ));
    }
    public byte[] exportAppointmentsToPdf() {
        List<Appointment> appointments = getAllAppointments();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Créer un flux de sortie pour le PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            // Initialiser le document PDF
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Ajouter un titre
            document.add(new Paragraph("Appointments List")
                    .setFontSize(18)
                    .setBold());

            // Créer un tableau avec 7 colonnes
            Table table = new Table(7);
            table.setWidth(500); // Largeur fixe pour le tableau

            // Ajouter les en-têtes
            table.addHeaderCell("ID");
            table.addHeaderCell("Psychiatrist");
            table.addHeaderCell("Student");
            table.addHeaderCell("Date");
            table.addHeaderCell("Start Time");
            table.addHeaderCell("End Time");
            table.addHeaderCell("Status");

            // Ajouter les données des rendez-vous
            for (Appointment appointment : appointments) {
                table.addCell(String.valueOf(appointment.getId()));
                table.addCell(appointment.getPsychiatrist());
                table.addCell(appointment.getStudent());
                table.addCell(dateFormat.format(appointment.getDate()));
                table.addCell(appointment.getStartTime());
                table.addCell(appointment.getEndTime());
                table.addCell(appointment.getStatus().toString());
            }

            // Ajouter le tableau au document
            document.add(table);

            // Fermer le document
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage());
        }

        return baos.toByteArray();
    }
    // get by id
    public Appointment getAppointmentById(int id) {
        return appointmentRepository.findById(id).orElse(null);
    }
}
