package com.esprit.microservice.activities;

import com.esprit.microservice.activities.Entity.Activity;
import com.esprit.microservice.activities.Repository.IActivityRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
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
			// Ajout de 4 activités par défaut
			activityRepository.save(new Activity(
					"Atelier de peinture",
					"Un atelier créatif pour explorer l'art de la peinture",
					Activity.Category.WORKSHOP
			));

			activityRepository.save(new Activity(
					"Groupe de soutien hebdomadaire",
					"Rencontre pour partager et soutenir les membres",
					Activity.Category.SUPPORT_GROUP
			));

			activityRepository.save(new Activity(
					"Séance de thérapie individuelle",
					"Session personnalisée avec un thérapeute",
					Activity.Category.THERAPY
			));

			activityRepository.save(new Activity(
					"Cours de yoga matinal",
					"Exercice de yoga pour bien commencer la journée",
					Activity.Category.EXERCISE
			));
		};
		}
	}