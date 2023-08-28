package com.licenta.aplicatie.repository;

import com.licenta.aplicatie.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    @Query("SELECT a FROM Admin a WHERE a.user.email = :email")
    Admin findByUserEmail(@Param("email") String email);
}