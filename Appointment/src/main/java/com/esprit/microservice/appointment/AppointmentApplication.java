package com.esprit.microservice.appointment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.esprit.microservice.appointment.Entity.Appointment;
import com.esprit.microservice.appointment.Repository.AppointmentRepository;

import java.util.Date;

@SpringBootApplication
public class AppointmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppointmentApplication.class, args);
	}

	@Autowired
	private AppointmentRepository repository;

	@Bean
	ApplicationRunner init() {
		return args -> {
			// save
			repository.save(new Appointment(
					"Dr. Smith",           // psychiatrist
					"John Doe",           // student
					new Date(),           // date
					"10:00",             // startTime
					"10:30"              // endTime
			));

			// fetch
			repository.findAll().forEach(System.out::println);
		};
	}
}