package com.licenta.aplicatie.controller;

import com.licenta.aplicatie.auth.StudyYearRequest;
import com.licenta.aplicatie.auth.StudyYearResponse;
import com.licenta.aplicatie.models.StudyYear;
import com.licenta.aplicatie.service.StudyYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments/{departmentId}/studyYears")
public class StudyYearController {

    private final StudyYearService studyYearService;

    @Autowired
    public StudyYearController(StudyYearService studyYearService) {
        this.studyYearService = studyYearService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StudyYear> addStudyYear(@PathVariable Integer departmentId, @RequestBody StudyYear studyYear) {
        StudyYear addedStudyYear = studyYearService.addStudyYear(departmentId, studyYear);
        return new ResponseEntity<>(addedStudyYear, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<StudyYearResponse>> getAllStudyYearsForDepartment(@PathVariable Integer departmentId) {
        List<StudyYearResponse> studyYears = studyYearService.getAllStudyYearsForDepartment(departmentId);
        return ResponseEntity.ok(studyYears);
    }


}
