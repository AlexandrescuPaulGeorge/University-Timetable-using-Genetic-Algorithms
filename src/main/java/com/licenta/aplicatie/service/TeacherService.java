package com.licenta.aplicatie.service;

import com.licenta.aplicatie.auth.TeacherResponse;
import com.licenta.aplicatie.models.Teacher;
import com.licenta.aplicatie.repository.DepartmentRepository;
import com.licenta.aplicatie.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository, DepartmentRepository departmentRepository) {
        this.teacherRepository = teacherRepository;
        this.departmentRepository = departmentRepository;
    }

    public Teacher addTeacher(Integer departmentId, Teacher teacher) {
        return departmentRepository.findById(departmentId).map(department -> {
            teacher.setDepartment(department);
            return teacherRepository.save(teacher);
        }).orElseThrow(() -> new RuntimeException("No department with matching id found"));
    }

    public List<TeacherResponse> getAllTeacherNames() {
        return teacherRepository.findAll()
                .stream()
                .map(teacher -> new TeacherResponse(teacher.getFirstname(), teacher.getLastname()))
                .collect(Collectors.toList());
    }
}
