package com.esprit.microservice.forum.Entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Forum implements Serializable {
    @Id
    @GeneratedValue
    private int id;
    private String title;
    private String subject;

    public enum MentalIssue {
        ANXIETY,
        DEPRESSION,
        BIPOLAR,
        SCHIZOPHRENIA,
        OCD
    }

    @Enumerated(EnumType.STRING)
    private MentalIssue tags;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

    public MentalIssue getTags() {
        return tags;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTags(MentalIssue tags) {
        this.tags = tags;
    }

    public Forum() {
        super();
    }

    public Forum(String title) {
        super();
        this.title = title;
    }

    public Forum(String title, String subject, MentalIssue tags) {
        this.title = title;
        this.subject = subject;
        this.tags = tags;
    }
}