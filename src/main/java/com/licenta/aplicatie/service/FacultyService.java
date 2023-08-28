package com.licenta.aplicatie.service;

import com.licenta.aplicatie.auth.FacultyResponse;
import com.licenta.aplicatie.models.Faculty;
import com.licenta.aplicatie.models.University;
import com.licenta.aplicatie.repository.FacultyRepository;
import com.licenta.aplicatie.repository.UniversityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final UniversityRepository universityRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository, UniversityRepository universityRepository) {
        this.facultyRepository = facultyRepository;
        this.universityRepository = universityRepository;
    }

    public Faculty addFaculty(Integer universityId, Faculty faculty) {
        University university = universityRepository.findById(universityId)
                .orElseThrow(() -> new RuntimeException("No university with matching id found"));

        Faculty existingFaculty = facultyRepository.findByNameAndUniversity(faculty.getName(), university);
        if (existingFaculty != null) {
            throw new IllegalArgumentException("A faculty with the same name already exists for this university");
        }

        faculty.setUniversity(university);
        return facultyRepository.save(faculty);
    }
    public List<FacultyResponse> getFacultiesByUniversityId(Integer universityId) {
        List<Faculty> faculties = facultyRepository.findByUniversityId(universityId);

        return faculties.stream()
                .map(faculty -> new FacultyResponse(faculty.getName()))
                .collect(Collectors.toList());
    }
}
