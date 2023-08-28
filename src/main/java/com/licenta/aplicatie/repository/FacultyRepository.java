package com.licenta.aplicatie.repository;

import com.licenta.aplicatie.models.Faculty;
import com.licenta.aplicatie.models.University;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Integer> {
    Faculty findByNameAndUniversity(String name, University university);
    List<Faculty> findByUniversityId(Integer universityId);
}

