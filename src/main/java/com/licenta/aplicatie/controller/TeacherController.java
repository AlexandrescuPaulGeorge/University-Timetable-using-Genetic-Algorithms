package com.licenta.aplicatie.controller;

import com.licenta.aplicatie.auth.TeacherRequest;
import com.licenta.aplicatie.auth.TeacherResponse;
import com.licenta.aplicatie.models.Teacher;
import com.licenta.aplicatie.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments/{departmentId}/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Teacher> addTeacher(@PathVariable Integer departmentId, @RequestBody TeacherRequest teacherRequest) {
        Teacher teacher = new Teacher();
        teacher.setEmail(teacherRequest.getEmail());
        teacher.setFirstname(teacherRequest.getFirstname());
        teacher.setLastname(teacherRequest.getLastname());
        Teacher addedTeacher = teacherService.addTeacher(departmentId, teacher);
        return new ResponseEntity<>(addedTeacher, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<TeacherResponse>> getAllTeacherNames() {
        List<TeacherResponse> teacherNames = teacherService.getAllTeacherNames();
        return ResponseEntity.ok(teacherNames);
    }
}

