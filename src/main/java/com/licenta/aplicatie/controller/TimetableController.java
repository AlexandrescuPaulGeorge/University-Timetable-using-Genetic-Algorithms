package com.licenta.aplicatie.controller;

import com.licenta.aplicatie.schedule.Timetable;
import com.licenta.aplicatie.service.TimetableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/timetable")
public class TimetableController {
    private final TimetableService timetableService;

    @Autowired
    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @PostMapping("/loadData")
    public ResponseEntity<Void> loadData() {
        timetableService.loadData();
        return ResponseEntity.ok().build();
    }

//    @GetMapping
//    public ResponseEntity<Timetable> getTimetable() {
//        Timetable timetable = timetableService.generateTimetable();
//        return ResponseEntity.ok(timetable);
//    }
}

