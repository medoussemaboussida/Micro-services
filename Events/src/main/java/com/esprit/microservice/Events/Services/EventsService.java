package com.esprit.microservice.Events.Services;

import com.esprit.microservice.Events.Entity.Events;
import com.esprit.microservice.Events.Repository.EventRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.itextpdf.layout.Document; // Import correct
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventsService {

    @Autowired
    private EventRepository eventsRepository;

    @Autowired
    private RestTemplate restTemplate; // Injection de RestTemplate

    @Value("${weather.api.key}")
    private String WEATHER_API_KEY; // Injection de la clé API depuis application.properties

    @Value("${google.maps.api.key}")
    private String GOOGLE_MAPS_API_KEY; // Nouvelle clé API


    private static final String WEATHER_API_URL = "http://api.openweathermap.org/data/2.5/weather?q={city}&appid={apiKey}&units=metric";
    private static final String GOOGLE_GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json?address={address}&key={apiKey}";
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

    // Recherche par type d'événement
    public List<Events> getByEventType(String eventType) {
        return eventsRepository.findByEventType(eventType);
    }

    // Recherche par localisation
    public List<Events> getByLocalisation(String localisation) {
        return eventsRepository.findByLocalisation(localisation);
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

    // Méthode pour récupérer la météo
    public Map<String, Object> getWeatherForEvent(int id) {
        Events event = getEventById(id);
        if (event == null || event.getLocalisation() == null) {
            return null;
        }
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("city", event.getLocalisation());
        uriVariables.put("apiKey", WEATHER_API_KEY);
        try {
            Map<String, Object> weatherData = restTemplate.getForObject(WEATHER_API_URL, Map.class, uriVariables);
            return weatherData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public byte[] generateEventPdf(int id) {
        Events event = getEventById(id);
        if (event == null) {
            return null;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Créer un document PDF
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Ajouter les détails de l'événement au PDF
            document.add(new Paragraph("Détails de l'événement").setBold());
            document.add(new Paragraph("Titre: " + event.getTitle()));
            document.add(new Paragraph("Description: " + event.getDescription()));
            document.add(new Paragraph("Date de début: " + event.getStartDate()));
            document.add(new Paragraph("Date de fin: " + event.getEndDate()));
            document.add(new Paragraph("Heure: " + event.getHeure()));
            document.add(new Paragraph("Localisation: " + (event.getLocalisation() != null ? event.getLocalisation() : "Non spécifiée")));
            document.add(new Paragraph("Type: " + event.getEventType()));
            document.add(new Paragraph("Email de contact: " + event.getContactEmail()));
            if (event.getOnlineLink() != null && !event.getOnlineLink().isEmpty()) {
                document.add(new Paragraph("Lien en ligne: " + event.getOnlineLink()));
            }

            // Fermer le document
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




    public Map<String, Double> getCoordinatesForEvent(String localisation) {
        if (localisation == null || localisation.isEmpty()) {
            System.out.println("Localisation vide ou null.");
            return null;
        }
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("address", localisation);
        uriVariables.put("apiKey", GOOGLE_MAPS_API_KEY);
        try {
            Map<String, Object> geoData = restTemplate.getForObject(GOOGLE_GEOCODING_URL, Map.class, uriVariables);
            if (geoData != null && "OK".equals(geoData.get("status"))) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) geoData.get("results");
                if (!results.isEmpty()) {
                    Map<String, Object> geometry = (Map<String, Object>) results.get(0).get("geometry");
                    Map<String, Double> location = (Map<String, Double>) geometry.get("location");
                    Map<String, Double> coordinates = new HashMap<>();
                    coordinates.put("lat", location.get("lat"));
                    coordinates.put("lng", location.get("lng"));
                    return coordinates;
                }
            }
            System.out.println("Coordonnées non trouvées pour " + localisation);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}