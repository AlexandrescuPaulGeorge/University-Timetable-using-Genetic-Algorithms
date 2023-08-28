package com.licenta.aplicatie;

import com.licenta.aplicatie.repository.TimetableRepository;
import com.licenta.aplicatie.schedule.FitnessFunction;
import com.licenta.aplicatie.schedule.GeneticAlgorithm;
import com.licenta.aplicatie.schedule.Timetable;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class AplicatieApplication {

	public static void main(String[] args) {
		SpringApplication.run(AplicatieApplication.class, args);
	}
}

