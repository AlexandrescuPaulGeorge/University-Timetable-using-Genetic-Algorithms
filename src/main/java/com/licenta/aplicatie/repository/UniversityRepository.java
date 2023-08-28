package com.licenta.aplicatie.repository;

import com.licenta.aplicatie.models.Admin;
import com.licenta.aplicatie.models.University;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UniversityRepository extends JpaRepository<University, Integer> {
    University findByNameAndCreatedByAdmin(String name, Admin admin);
    List<University> findAllByCreatedByAdmin(Admin admin);
}
