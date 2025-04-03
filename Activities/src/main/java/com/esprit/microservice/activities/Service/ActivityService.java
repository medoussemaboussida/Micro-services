package com.esprit.microservice.activities.Service;

import com.esprit.microservice.activities.Entity.Activity;
import com.esprit.microservice.activities.Repository.IActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {

    @Autowired
    private IActivityRepository activityRepository;

    // CREATE : Ajouter une nouvelle activité
    public Activity createActivity(Activity activity) {
        // Vérification si le titre existe déjà (car il doit être unique)
        if (activityRepository.findByTitle(activity.getTitle()) != null) {
            throw new IllegalArgumentException("Une activité avec ce titre existe déjà.");
        }
        return activityRepository.save(activity);
    }

    // READ : Récupérer toutes les activités
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    // READ : Récupérer une activité par ID
    public Activity getActivityById(Long id) {
        Optional<Activity> activity = activityRepository.findById(id);
        if (activity.isPresent()) {
            return activity.get();
        } else {
            throw new RuntimeException("Activité avec l'ID " + id + " non trouvée.");
        }
    }

    // READ : Récupérer une activité par titre (optionnel)
    public Activity getActivityByTitle(String title) {
        Activity activity = activityRepository.findByTitle(title);
        if (activity != null) {
            return activity;
        } else {
            throw new RuntimeException("Activité avec le titre " + title + " non trouvée.");
        }
    }

    // UPDATE : Mettre à jour une activité existante
    public Activity updateActivity(Long id, Activity updatedActivity) {
        Optional<Activity> existingActivity = activityRepository.findById(id);
        if (existingActivity.isPresent()) {
            Activity activity = existingActivity.get();
            // Vérifier si le nouveau titre est unique (s'il change)
            if (!activity.getTitle().equals(updatedActivity.getTitle()) &&
                    activityRepository.findByTitle(updatedActivity.getTitle()) != null) {
                throw new IllegalArgumentException("Une autre activité avec ce titre existe déjà.");
            }
            // Mettre à jour les champs
            activity.setTitle(updatedActivity.getTitle());
            activity.setDescription(updatedActivity.getDescription());
            activity.setCategory(updatedActivity.getCategory());
            activity.setImageUrl(updatedActivity.getImageUrl());
            // createdAt ne doit pas être modifié (updatable = false)
            return activityRepository.save(activity);
        } else {
            throw new RuntimeException("Activité avec l'ID " + id + " non trouvée.");
        }
    }

    // DELETE : Supprimer une activité par ID
    public void deleteActivity(Long id) {
        if (activityRepository.existsById(id)) {
            activityRepository.deleteById(id);
        } else {
            throw new RuntimeException("Activité avec l'ID " + id + " non trouvée.");
        }
    }
}