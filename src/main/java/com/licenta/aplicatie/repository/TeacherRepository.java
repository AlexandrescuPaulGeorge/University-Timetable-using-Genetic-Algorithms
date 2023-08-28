package com.licenta.aplicatie.repository;

import com.licenta.aplicatie.models.Teacher;
import com.licenta.aplicatie.models.University;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
}
