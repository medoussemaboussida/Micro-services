package com.esprit.microservice.activities.Entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Activity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private String description;

    // Enum pour les catégories
    public enum Category {
        WORKSHOP,       // Atelier
        SUPPORT_GROUP,  // Groupe de soutien
        THERAPY,        // Thérapie
        EXERCISE,       // Exercice physique
        MEDITATION      // Méditation
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category; // Remplace la relation ManyToOne par un enum

    private String imageUrl;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    // Constructeurs
    public Activity() {
        this.createdAt = new Date();
    }

    public Activity(String title, String description, Category category) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.createdAt = new Date();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}