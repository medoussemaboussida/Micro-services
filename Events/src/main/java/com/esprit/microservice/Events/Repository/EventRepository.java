package com.esprit.microservice.Events.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.esprit.microservice.Events.Entity.Events;

import java.util.List;

public interface EventRepository extends JpaRepository<Events, Integer> {
    // Recherche par titre
    List<Events> findByTitle(String title);

    // Recherche paginée par titre avec une requête JPQL
    @Query("select e from Events e where e.title like :title")
    public Page<Events> eventsByTitle(@Param("title") String n, Pageable pageable);
}