package com.licenta.aplicatie.controller;

import com.licenta.aplicatie.auth.FacultyResponse;
import com.licenta.aplicatie.models.Faculty;
import com.licenta.aplicatie.service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/universities/{universityId}/faculties")
public class FacultyController {

    private final FacultyService facultyService;

    @Autowired
    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Faculty> addFaculty(@PathVariable Integer universityId, @RequestBody Faculty faculty) {
        Faculty addedFaculty = facultyService.addFaculty(universityId, faculty);
        return new ResponseEntity<>(addedFaculty, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<FacultyResponse>> getFacultiesByUniversityId(@PathVariable Integer universityId) {
        List<FacultyResponse> faculties = facultyService.getFacultiesByUniversityId(universityId);
        return new ResponseEntity<>(faculties, HttpStatus.OK);
    }
}
