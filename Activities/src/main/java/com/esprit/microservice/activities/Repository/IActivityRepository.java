package com.esprit.microservice.activities.Repository;

import com.esprit.microservice.activities.Entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IActivityRepository extends JpaRepository<Activity, Long> {
    // Méthode personnalisée pour trouver une activité par titre
    Activity findByTitle(String title);
}