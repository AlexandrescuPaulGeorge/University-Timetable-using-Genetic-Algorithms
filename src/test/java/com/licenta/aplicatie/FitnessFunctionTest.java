package com.licenta.aplicatie;

import com.licenta.aplicatie.models.*;
import com.licenta.aplicatie.repository.TimetableRepository;
import com.licenta.aplicatie.schedule.*;
import com.licenta.aplicatie.service.TimetableService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FitnessFunctionTest {

    @Autowired
    private FitnessFunction fitnessFunction;

    @Autowired
    private TimetableRepository timetableRepository;

    @Test
    public void testCalculateFitness() {
        Timetable timetable = timetableRepository.findById(3).orElse(null);
        assertNotNull(timetable);

        double fitness = fitnessFunction.calculateFitness(timetable);

    }
}






