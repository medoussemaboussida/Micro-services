package com.esprit.microservice.Events.Repository;

import com.esprit.microservice.Events.Entity.Events;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface EventRepository extends JpaRepository<Events, Integer> {
    List<Events> findByTitle(String title);
    List<Events> findByEventType(String eventType);
    List<Events> findByLocalisation(String localisation);
    List<Events> findByStatus(String status);
    List<Events> findByStartDate(Date startDate);
    List<Events> findByStartDateBetween(Date startDate, Date endDate);
    List<Events> findByTitleContainingIgnoreCase(String keyword);
    List<Events> findByLocalisationAndEventType(String localisation, String eventType);
}