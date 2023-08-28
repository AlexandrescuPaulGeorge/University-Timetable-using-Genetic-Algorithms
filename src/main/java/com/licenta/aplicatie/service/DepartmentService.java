package com.licenta.aplicatie.service;

import com.licenta.aplicatie.models.Department;
import com.licenta.aplicatie.models.Faculty;
import com.licenta.aplicatie.repository.DepartmentRepository;
import com.licenta.aplicatie.repository.FacultyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository, FacultyRepository facultyRepository) {
        this.departmentRepository = departmentRepository;
        this.facultyRepository = facultyRepository;
    }

    public Department addDepartment(Integer facultyId, Department department) {
        return facultyRepository.findById(facultyId).map(faculty -> {
            department.setFaculty(faculty);
            return departmentRepository.save(department);
        }).orElseThrow(()-> new RuntimeException("No faculty with matching id found"));
    }
    public List<Department> getDepartmentsByFacultyId(Integer facultyId) {
        return facultyRepository.findById(facultyId)
                .map(Faculty::getDepartments)
                .orElseThrow(() -> new RuntimeException("No faculty with matching id found"));
    }

    public List<String> getDepartmentNames(Integer facultyId) {
        return facultyRepository.findById(facultyId)
                .orElseThrow(() -> new RuntimeException("No faculty with matching id found"))
                .getDepartments()
                .stream()
                .map(Department::getName)
                .collect(Collectors.toList());
    }

}
