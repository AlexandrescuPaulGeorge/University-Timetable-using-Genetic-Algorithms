package com.licenta.aplicatie.controller;

import com.licenta.aplicatie.auth.SubjectRequest;
import com.licenta.aplicatie.auth.SubjectResponse;
import com.licenta.aplicatie.models.Subject;
import com.licenta.aplicatie.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studyYears/{studyYearId}/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    @Autowired
    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Subject> addSubject(@PathVariable Integer studyYearId, @RequestBody SubjectRequest subjectRequest) {
        Subject addedSubject = subjectService.addSubject(studyYearId, subjectRequest);
        return new ResponseEntity<>(addedSubject, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<SubjectResponse>> getSubjectNamesByStudyYearId(@PathVariable Integer studyYearId) {
        List<SubjectResponse> subjectNames = subjectService.getSubjectNamesByStudyYearId(studyYearId);
        return new ResponseEntity<>(subjectNames, HttpStatus.OK);
    }
}
