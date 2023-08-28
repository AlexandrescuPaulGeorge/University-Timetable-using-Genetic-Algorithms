package com.licenta.aplicatie.repository;

import com.licenta.aplicatie.models.RoleName;
import com.licenta.aplicatie.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(RoleName roleName);
}





