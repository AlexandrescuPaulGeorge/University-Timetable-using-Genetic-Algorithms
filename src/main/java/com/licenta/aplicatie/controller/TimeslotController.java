package com.licenta.aplicatie.controller;

import com.licenta.aplicatie.schedule.Timeslot;
import com.licenta.aplicatie.schedule.Timetable;
import com.licenta.aplicatie.service.TimeslotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/timeslots")
public class TimeslotController {
    private final TimeslotService timeslotService;

    @Autowired
    public TimeslotController(TimeslotService timeslotService) {
        this.timeslotService = timeslotService;
    }

    @PostMapping("/random")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Timetable> generateRandomTimeslots() {
        timeslotService.generateRandomTimeslots();
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
