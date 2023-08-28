package com.licenta.aplicatie.controller;

import com.licenta.aplicatie.schedule.Timetable;
import com.licenta.aplicatie.service.GeneticAlgorithmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/geneticalgorithm")
public class GeneticAlgorithmController {
    private final GeneticAlgorithmService geneticAlgorithmService;

    @Autowired
    public GeneticAlgorithmController(GeneticAlgorithmService geneticAlgorithmService) {
        this.geneticAlgorithmService = geneticAlgorithmService;
    }

    @PostMapping("/run")
    public ResponseEntity<Timetable> runGeneticAlgorithm() {
       geneticAlgorithmService.runGeneticAlgorithm();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
