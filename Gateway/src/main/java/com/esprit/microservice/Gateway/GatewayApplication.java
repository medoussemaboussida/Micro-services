package com.esprit.microservice.Gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				// Route pour le microservice "candidat"
				.route("candidat", r -> r.path("/candidats/**")
						.uri("lb://candidat-service")) // Utilisation de Load Balancer avec Eureka
				// Route pour le microservice "forum"
				.route("forum", r -> r.path("/forums/**")
						.uri("lb://forum-service")) // Utilisation de Load Balancer avec Eureka
				// Route pour le microservice "Publication"
				.route("publication", r -> r.path("/publications/**")
						.uri("lb://publication-service")) // Utilisation de Load Balancer avec Eureka
				// Route pour le microservice "Events"
				.route("events", r -> r.path("/Events/**")
						.uri("lb://events-service")) // Utilisation de Load Balancer avec Eureka
				.build();
	}
}