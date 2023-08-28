package com.licenta.aplicatie.repository;

import com.licenta.aplicatie.models.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuperAdminRepository extends JpaRepository<SuperAdmin, Integer> {
}