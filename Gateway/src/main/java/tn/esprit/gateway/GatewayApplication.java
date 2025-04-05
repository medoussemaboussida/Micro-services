package tn.esprit.gateway;

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
                .route("candidat", r -> r.path("/candidats/**")
                        .uri("http://localhost:8082/")) // Load Balancer vers le microservice "MS-CANDIDAT"
                .route("forum", r -> r.path("/forums/**")
                        .uri("http://localhost:8055/")) // Load Balancer vers le microservice "MS-forum"
                .route("Publication", r -> r.path("/publications/**")
                        .uri("http://localhost:8056/"))
                .build();
    }

}
