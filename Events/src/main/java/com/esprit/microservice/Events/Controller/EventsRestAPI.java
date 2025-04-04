
package com.esprit.microservice.Events.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.esprit.microservice.Events.Entity.Events;
import com.esprit.microservice.Events.Services.EventsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/Events")
public class EventsRestAPI {

    @Autowired
    private EventsService eventsService;

    // Créer un événement
    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Events> createEvent(@RequestBody Events event) {
        return new ResponseEntity<>(eventsService.addEvent(event), HttpStatus.OK);
    }

    // Mettre à jour un événement
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Events> updateEvent(@PathVariable(value = "id") int id, @RequestBody Events event) {
        return new ResponseEntity<>(eventsService.updateEvent(id, event), HttpStatus.OK);
    }

    // Supprimer un événement
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteEvent(@PathVariable(value = "id") int id) {
        return new ResponseEntity<>(eventsService.deleteEvent(id), HttpStatus.OK);
    }

    // Récupérer tous les événements
    @GetMapping("/Events")
    public ResponseEntity<List<Events>> getAllEvents() {
        return new ResponseEntity<>(eventsService.getAllEvents(), HttpStatus.OK);
    }

    // Récupérer les événements par titre
    @GetMapping("/by-title/{title}")
    public ResponseEntity<List<Events>> getEventsByTitle(@PathVariable("title") String title) {
        List<Events> events = eventsService.getByTitle(title);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    // Récupérer un événement par ID
    @GetMapping("/{id}")
    public ResponseEntity<Events> getEventById(@PathVariable("id") int id) {
        Events event = eventsService.getEventById(id);
        if (event != null) {
            return new ResponseEntity<>(event, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}