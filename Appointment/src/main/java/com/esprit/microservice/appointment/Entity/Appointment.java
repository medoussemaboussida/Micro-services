package com.esprit.microservice.appointment.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.util.Date;

@Entity
public class Appointment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    @NotEmpty(message = "Psychiatrist cannot be empty")
    private String psychiatrist;

    @Column(nullable = false)
    @NotEmpty(message = "Student cannot be empty")
    private String student;

    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(nullable = false)
    private String startTime;

    @Column(nullable = false)
    private String endTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    // Enum for status
    public enum Status {
        PENDING, CONFIRMED, CANCELED, COMPLETED
    }

    // Constructeurs
    public Appointment() {
        super();
        this.status = Status.PENDING; // Default value
    }

    public Appointment(String psychiatrist, String student, Date date) {
        this.psychiatrist = psychiatrist;
        this.student = student;
        this.date = date;
        this.status = Status.PENDING;
    }

    public Appointment(String psychiatrist, String student, Date date, String startTime, String endTime) {
        this.psychiatrist = psychiatrist;
        this.student = student;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = Status.PENDING;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPsychiatrist() {
        return psychiatrist;
    }

    public void setPsychiatrist(String psychiatrist) {
        this.psychiatrist = psychiatrist;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}