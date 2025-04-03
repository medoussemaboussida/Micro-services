package com.esprit.microservice.activities.Controller;

import com.esprit.microservice.activities.Entity.Activity;
import com.esprit.microservice.activities.Service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities") // Préfixe de base pour tous les endpoints
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    // CREATE : Ajouter une nouvelle activité
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Activity> createActivity(@RequestBody Activity activity) {
        Activity createdActivity = activityService.createActivity(activity);
        return new ResponseEntity<>(createdActivity, HttpStatus.OK); // ou HttpStatus.CREATED
    }

    // READ : Récupérer toutes les activités
    @GetMapping
    public ResponseEntity<List<Activity>> getAllActivities() {
        List<Activity> activities = activityService.getAllActivities();
        return new ResponseEntity<>(activities, HttpStatus.OK); // 200
    }

    // READ : Récupérer une activité par ID
    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
        try {
            Activity activity = activityService.getActivityById(id);
            return new ResponseEntity<>(activity, HttpStatus.OK); // 200
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // 404
        }
    }

    // READ : Récupérer une activité par titre
    @GetMapping("/title/{title}")
    public ResponseEntity<Activity> getActivityByTitle(@PathVariable String title) {
        try {
            Activity activity = activityService.getActivityByTitle(title);
            return new ResponseEntity<>(activity, HttpStatus.OK); // 200
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // 404
        }
    }

    // UPDATE : Mettre à jour une activité existante
    @PutMapping("/{id}")
    public ResponseEntity<Activity> updateActivity(@PathVariable Long id, @RequestBody Activity activity) {
        try {
            Activity updatedActivity = activityService.updateActivity(id, activity);
            return new ResponseEntity<>(updatedActivity, HttpStatus.OK); // 200
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT); // 409 si titre déjà utilisé
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // 404
        }
    }

    // DELETE : Supprimer une activité
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return new ResponseEntity<>("Activité supprimée avec succès", HttpStatus.OK);
    }
}