package com.licenta.aplicatie.controller;

import com.licenta.aplicatie.models.Department;
import com.licenta.aplicatie.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faculties/{facultyId}/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Department> addDepartment(@PathVariable Integer facultyId, @RequestBody Department department) {
        Department addedDepartment = departmentService.addDepartment(facultyId, department);
        return new ResponseEntity<>(addedDepartment, HttpStatus.CREATED);
    }
    @GetMapping("/details")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Department>> getDepartmentsByFacultyId(@PathVariable Integer facultyId) {
        return ResponseEntity.ok(departmentService.getDepartmentsByFacultyId(facultyId));
    }

    @GetMapping
    public ResponseEntity<List<String>> getDepartmentNames(@PathVariable Integer facultyId) {
        List<String> departmentNames = departmentService.getDepartmentNames(facultyId);
        return ResponseEntity.ok(departmentNames);
    }
}
