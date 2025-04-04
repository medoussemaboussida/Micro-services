package com.esprit.microservice.Events;

import com.esprit.microservice.Events.Repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.esprit.microservice.Events.Entity.Events;

import java.util.Date;

@SpringBootApplication
public class EventsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventsApplication.class, args);
	}

	@Autowired
	private EventRepository repository;

	@Bean
	ApplicationRunner init() {
		return args -> {
			// Insertion de données initiales
			repository.save(new Events(
					"Conférence sur l'IA",
					"Discussion sur les avancées en intelligence artificielle",
					new Date(),
					new Date(System.currentTimeMillis() + 86400000), // Date de fin +1 jour
					"14:00",
					"contact@event.tn",
					"in-person"
			));
			repository.save(new Events(
					"Webinaire Java",
					"Atelier sur les bases de Spring Boot",
					new Date(),
					new Date(System.currentTimeMillis() + 3600000), // Date de fin +1 heure
					"10:00",
					"webinar@event.tn",
					"online"
			));
			repository.save(new Events(
					"Meetup Tech",
					"Rencontre des développeurs locaux",
					new Date(),
					new Date(System.currentTimeMillis() + 7200000), // Date de fin +2 heures
					"18:00",
					"tech@event.tn",
					"in-person"
			));

			// Affichage des données insérées
			repository.findAll().forEach(System.out::println);
		};
	}
}