package com.licenta.aplicatie.service;

import com.licenta.aplicatie.repository.TimetableRepository;
import com.licenta.aplicatie.schedule.ExcelExporter;
import com.licenta.aplicatie.schedule.GeneticAlgorithm;
import com.licenta.aplicatie.schedule.Timetable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneticAlgorithmService {
    private final GeneticAlgorithm geneticAlgorithm;

    @Autowired
    private ExcelExporter excelExporter;

    @Autowired
    public GeneticAlgorithmService(GeneticAlgorithm geneticAlgorithm) {
        this.geneticAlgorithm = geneticAlgorithm;
    }

    public void runGeneticAlgorithm() {
        geneticAlgorithm.run();
        excelExporter.exportTimetable("C:\\Users\\Paul\\Desktop\\Result\\timetable.xlsx");
    }
}
