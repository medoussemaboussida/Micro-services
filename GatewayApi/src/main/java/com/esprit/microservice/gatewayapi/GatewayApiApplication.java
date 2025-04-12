package com.esprit.microservice.gatewayapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApiApplication.class, args);
	}
	@Bean
	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("candidat", r -> r.path("/candidats/**")
						.uri("lb://candidat-service"))
				.route("forum", r -> r.path("/forum/**", "/forums/**") // Match both patterns
						.uri("http://localhost:8055/")) // Updated to match spring.application.name=forum
				.route("publication", r -> r.path("/publications/**")
						.uri("lb://publication-service"))
				.build();
	}
}
