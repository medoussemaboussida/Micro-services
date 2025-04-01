package com.esprit.microservice.forum;

import com.esprit.microservice.forum.Entity.Forum;
import com.esprit.microservice.forum.Repository.ForumRepository;
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
@OpenAPIDefinition(info = @Info(title = "ForumMicroService", description = "gestion des forums",contact = @Contact(name = "4Twin7",url = "http://google.com",email = "medoussemaboussida@gmail.com")))
public class ForumApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForumApplication.class, args);
    }

    @Autowired
    private ForumRepository repository;

    @Bean
    ApplicationRunner init() {
        return args -> {
            // save
            repository.save(new Forum("Anxiety Support", "Discussion about anxiety issues", Forum.MentalIssue.ANXIETY));
            repository.save(new Forum("Depression Help", "Support for depression", Forum.MentalIssue.DEPRESSION));
            repository.save(new Forum("Bipolar Community", "Bipolar disorder talk", Forum.MentalIssue.BIPOLAR));
            repository.save(new Forum("OCD Strategies", "Managing OCD", Forum.MentalIssue.OCD));
            // fetch
            repository.findAll().forEach(System.out::println);
        };

    }
}