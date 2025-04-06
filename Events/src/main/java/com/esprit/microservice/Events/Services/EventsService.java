package com.esprit.microservice.Events.Services;

import com.esprit.microservice.Events.Entity.Events;
import com.esprit.microservice.Events.Repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class EventsService {

    @Autowired
    private EventRepository eventsRepository;

    // Ajout d'un événement
    public Events addEvent(Events event) {
        return eventsRepository.save(event);
    }

    // Mise à jour d'un événement
    public Events updateEvent(int id, Events newEvent) {
        if (eventsRepository.findById(id).isPresent()) {
            Events existingEvent = eventsRepository.findById(id).get();
            existingEvent.setTitle(newEvent.getTitle());
            existingEvent.setDescription(newEvent.getDescription());
            existingEvent.setStartDate(newEvent.getStartDate());
            existingEvent.setEndDate(newEvent.getEndDate());
            existingEvent.setLocalisation(newEvent.getLocalisation());
            existingEvent.setLieu(newEvent.getLieu());
            existingEvent.setHeure(newEvent.getHeure());
            existingEvent.setContactEmail(newEvent.getContactEmail());
            existingEvent.setEventType(newEvent.getEventType());
            existingEvent.setOnlineLink(newEvent.getOnlineLink());
            existingEvent.setImageUrl(newEvent.getImageUrl());
            existingEvent.setCreatedAt(newEvent.getCreatedAt());
            existingEvent.setStatus(newEvent.getStatus());
            existingEvent.setLat(newEvent.getLat());
            existingEvent.setLng(newEvent.getLng());
            return eventsRepository.save(existingEvent);
        } else {
            return null;
        }
    }

    // Suppression d'un événement
    public String deleteEvent(int id) {
        if (eventsRepository.findById(id).isPresent()) {
            eventsRepository.deleteById(id);
            return "événement supprimé";
        } else {
            return "événement non supprimé";
        }
    }

    // Récupération de tous les événements
    public List<Events> getAllEvents() {
        return eventsRepository.findAll();
    }

    // Récupération par titre
    public List<Events> getByTitle(String title) {
        return eventsRepository.findByTitle(title);
    }

    // Récupération par ID
    public Events getEventById(int id) {
        return eventsRepository.findById(id).orElse(null);
    }

    // Nouvelles méthodes de recherche et filtrage

    // Recherche par type d'événement


    // Recherche par localisation
    public List<Events> getByLocalisation(String localisation) {
        return eventsRepository.findByLocalisation(localisation);
    }
    public List<Events> getByEventType(String eventType) {
        return eventsRepository.findByEventType(eventType);
    }
    // Recherche par statut
    public List<Events> getByStatus(String status) {
        return eventsRepository.findByStatus(status);
    }

    // Filtrage par date de début
    public List<Events> getByStartDate(Date startDate) {
        return eventsRepository.findByStartDate(startDate);
    }

    // Filtrage par plage de dates
    public List<Events> getByDateRange(Date startDate, Date endDate) {
        return eventsRepository.findByStartDateBetween(startDate, endDate);
    }

    // Recherche par titre partiel (insensible à la casse)
    public List<Events> searchByTitleContaining(String keyword) {
        return eventsRepository.findByTitleContainingIgnoreCase(keyword);
    }

    // Filtrage par localisation et type d'événement
    public List<Events> getByLocalisationAndEventType(String localisation, String eventType) {
        return eventsRepository.findByLocalisationAndEventType(localisation, eventType);
    }
}