package com.licenta.aplicatie.repository;

import com.licenta.aplicatie.models.Department;
import com.licenta.aplicatie.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Integer> {
    List<Group> findByStudyYearId(Integer studyYearId);
}