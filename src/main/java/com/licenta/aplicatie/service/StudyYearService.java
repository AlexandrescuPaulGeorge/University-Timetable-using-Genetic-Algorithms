package com.licenta.aplicatie.service;

import com.licenta.aplicatie.auth.StudyYearRequest;
import com.licenta.aplicatie.auth.StudyYearResponse;
import com.licenta.aplicatie.models.Department;
import com.licenta.aplicatie.models.StudyYear;
import com.licenta.aplicatie.repository.DepartmentRepository;
import com.licenta.aplicatie.repository.StudyYearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudyYearService {

    private final StudyYearRepository studyYearRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public StudyYearService(StudyYearRepository studyYearRepository, DepartmentRepository departmentRepository) {
        this.studyYearRepository = studyYearRepository;
        this.departmentRepository = departmentRepository;
    }

    public StudyYear addStudyYear(Integer departmentId, StudyYear studyYear) {
        return departmentRepository.findById(departmentId).map(department -> {
            studyYear.setDepartment(department);
            return studyYearRepository.save(studyYear);
        }).orElseThrow(() -> new RuntimeException("No department with matching id found"));
    }
    public List<StudyYearResponse> getAllStudyYearsForDepartment(Integer departmentId) {
        return studyYearRepository.findByDepartmentId(departmentId)
                .stream()
                .map(studyYear -> new StudyYearResponse(studyYear.getYear()))
                .collect(Collectors.toList());
    }
}
