package tn.esprit.publication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import tn.esprit.publication.Entity.Publication;
import tn.esprit.publication.Repository.PublicationRepository;

@SpringBootApplication
@EnableDiscoveryClient
public class PublicationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PublicationApplication.class, args);
    }
    @Autowired
    private PublicationRepository repository;

    @Bean
    ApplicationRunner init() {
        return args -> {
            // save
            repository.save(new Publication("Publication 1", "Discussion about anxiety issues","Yassine"));

            // fetch
            repository.findAll().forEach(System.out::println);
        };

    }

}
