package com.esprit.microservice.Events.Controller;

import com.esprit.microservice.Events.Entity.Events;
import com.esprit.microservice.Events.Services.EventsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/Events")
public class EventsRestAPI {

    @Autowired
    private EventsService eventsService;

    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Events> createEvent(@RequestBody Events event) {
        return new ResponseEntity<>(eventsService.addEvent(event), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Events> updateEvent(@PathVariable(value = "id") int id, @RequestBody Events event) {
        Events updatedEvent = eventsService.updateEvent(id, event);
        if (updatedEvent != null) {
            return new ResponseEntity<>(updatedEvent, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteEvent(@PathVariable(value = "id") int id) {
        String result = eventsService.deleteEvent(id);
        if ("événement supprimé".equals(result)) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Events>> getAllEvents() {
        List<Events> events = eventsService.getAllEvents();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/by-title/{title}")
    public ResponseEntity<List<Events>> getEventsByTitle(@PathVariable("title") String title) {
        List<Events> events = eventsService.getByTitle(title);
        return new ResponseEntity<>(events, events.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Events> getEventById(@PathVariable("id") int id) {
        Events event = eventsService.getEventById(id);
        if (event != null) {
            return new ResponseEntity<>(event, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/by-event-type/{eventType}")
    public ResponseEntity<List<Events>> getByEventType(@PathVariable("eventType") String eventType) {
        List<Events> events = eventsService.getByEventType(eventType);
        return new ResponseEntity<>(events, events.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @GetMapping("/by-localisation/{localisation}")
    public ResponseEntity<List<Events>> getByLocalisation(@PathVariable("localisation") String localisation) {
        List<Events> events = eventsService.getByLocalisation(localisation);
        return new ResponseEntity<>(events, events.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<Events>> getByStatus(@PathVariable("status") String status) {
        List<Events> events = eventsService.getByStatus(status);
        return new ResponseEntity<>(events, events.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @GetMapping("/by-start-date")
    public ResponseEntity<List<Events>> getByStartDate(@RequestParam("startDate") Date startDate) {
        List<Events> events = eventsService.getByStartDate(startDate);
        return new ResponseEntity<>(events, events.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @GetMapping("/by-date-range")
    public ResponseEntity<List<Events>> getByDateRange(
            @RequestParam("startDate") Date startDate,
            @RequestParam("endDate") Date endDate) {
        List<Events> events = eventsService.getByDateRange(startDate, endDate);
        return new ResponseEntity<>(events, events.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Events>> searchByTitleContaining(@RequestParam("keyword") String keyword) {
        List<Events> events = eventsService.searchByTitleContaining(keyword);
        return new ResponseEntity<>(events, events.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @GetMapping("/by-localisation-and-type")
    public ResponseEntity<List<Events>> getByLocalisationAndEventType(
            @RequestParam("localisation") String localisation,
            @RequestParam("eventType") String eventType) {
        List<Events> events = eventsService.getByLocalisationAndEventType(localisation, eventType);
        return new ResponseEntity<>(events, events.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }
}