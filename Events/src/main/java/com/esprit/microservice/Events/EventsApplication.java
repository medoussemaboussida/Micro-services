package com.esprit.microservice.Events;

import com.esprit.microservice.Events.Entity.Events;
import com.esprit.microservice.Events.Repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@SpringBootApplication
@EnableDiscoveryClient
public class EventsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventsApplication.class, args);
	}

	@Autowired
	private EventRepository repository;

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	ApplicationRunner init() {
		return args -> {
			// Insertion de données initiales avec localisation
			Events event1 = new Events(
					"Conférence sur l'IA",
					"Discussion sur les avancées en intelligence artificielle",
					new Date(),
					new Date(System.currentTimeMillis() + 86400000), // Date de fin +1 jour
					"14:00",
					"contact@event.tn",
					"in-person"
			);
			event1.setLocalisation("Tunis");
			repository.save(event1);

			Events event2 = new Events(
					"Webinaire Java",
					"Atelier sur les bases de Spring Boot",
					new Date(),
					new Date(System.currentTimeMillis() + 3600000), // Date de fin +1 heure
					"10:00",
					"webinar@event.tn",
					"online"
			);
			event2.setLocalisation("Online");
			repository.save(event2);

			Events event3 = new Events(
					"Meetup Tech",
					"Rencontre des développeurs locaux",
					new Date(),
					new Date(System.currentTimeMillis() + 7200000), // Date de fin +2 heures
					"18:00",
					"tech@event.tn",
					"in-person"
			);
			event3.setLocalisation("Paris");
			repository.save(event3);

			// Affichage des données insérées
			repository.findAll().forEach(System.out::println);
		};
	}
}