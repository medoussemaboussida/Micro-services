package tn.esprit.job;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import tn.esprit.job.entity.Job;
import tn.esprit.job.repository.JobRepository;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "microservice atelier", description = "gestion des jobs",contact = @Contact(name = "4Twin7",url = "http://google.com",email = "medoussemaboussida@gmail.com")))
@EnableDiscoveryClient
public class JobApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobApplication.class, args);
    }
    @Autowired
    private JobRepository repository;
    @Bean
    ApplicationRunner init() {
        return args -> {

            repository.findAll().forEach(System.out::println);


        };
    }

}
