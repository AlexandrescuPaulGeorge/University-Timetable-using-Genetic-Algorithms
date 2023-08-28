package com.licenta.aplicatie.controller;

import com.licenta.aplicatie.auth.UniversityRequest;
import com.licenta.aplicatie.auth.UniversityResponse;
import com.licenta.aplicatie.models.Faculty;
import com.licenta.aplicatie.models.University;
import com.licenta.aplicatie.service.UniversityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/universities")
public class UniversityController {

    private final UniversityService universityService;

    @Autowired
    public UniversityController(UniversityService universityService) {
        this.universityService = universityService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<University> createUniversity(@RequestBody UniversityRequest universityRequest) {
        University university = universityService.addUniversity(universityRequest);
        return new ResponseEntity<>(university, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UniversityResponse>> getAllUniversities() {
        List<UniversityResponse> universityResponses = universityService.getAllUniversitiesForLoggedInAdmin();
        return new ResponseEntity<>(universityResponses, HttpStatus.OK);
    }

}

