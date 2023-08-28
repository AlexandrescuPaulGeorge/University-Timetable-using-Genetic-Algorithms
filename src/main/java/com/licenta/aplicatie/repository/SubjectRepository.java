package com.licenta.aplicatie.repository;

import com.licenta.aplicatie.models.StudyYear;
import com.licenta.aplicatie.models.Subject;
import com.licenta.aplicatie.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SubjectRepository  extends JpaRepository<Subject, Integer> {
    List<Subject> findByStudyYearId(Integer studyYearId);
    Optional<Subject> findByAcronymAndStudyYear(String acronym, StudyYear studyYear);
}
