package com.licenta.aplicatie.repository;

import com.licenta.aplicatie.models.StudyYear;
import com.licenta.aplicatie.models.University;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface StudyYearRepository extends JpaRepository<StudyYear, Integer> {
    Collection<StudyYear> findByDepartmentId(Integer departmentId);
}
