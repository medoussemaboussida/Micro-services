package com.esprit.microservice.activities;

import com.esprit.microservice.activities.Entity.Activity;
import com.esprit.microservice.activities.Repository.IActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ActivitiesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActivitiesApplication.class, args);
	}

	@Autowired
	private IActivityRepository activityRepository;

	@Bean
	ApplicationRunner init() {
		return args -> {
			Object[][] defaultActivities = {
					{"Atelier de peinture", "Un atelier créatif pour explorer l'art de la peinture", Activity.Category.WORKSHOP},
					{"Groupe de soutien hebdomadaire", "Rencontre pour partager et soutenir les membres", Activity.Category.SUPPORT_GROUP},
					{"Séance de thérapie individuelle", "Session personnalisée avec un thérapeute", Activity.Category.THERAPY},
					{"Cours de yoga matinal", "Exercice de yoga pour bien commencer la journée", Activity.Category.EXERCISE}
			};

			for (Object[] data : defaultActivities) {
				String title = (String) data[0];
				String description = (String) data[1];
				Activity.Category category = (Activity.Category) data[2];

				if (!activityRepository.existsByTitle(title)) {
					activityRepository.save(new Activity(title, description, category));
				}
			}
		};
	 }
 }