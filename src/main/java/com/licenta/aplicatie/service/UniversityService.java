package com.licenta.aplicatie.service;

import com.licenta.aplicatie.auth.UniversityRequest;
import com.licenta.aplicatie.auth.UniversityResponse;
import com.licenta.aplicatie.exceptions.ResourceNotFoundException;
import com.licenta.aplicatie.models.Admin;
import com.licenta.aplicatie.models.Faculty;
import com.licenta.aplicatie.models.University;
import com.licenta.aplicatie.repository.AdminRepository;
import com.licenta.aplicatie.repository.FacultyRepository;
import com.licenta.aplicatie.repository.UniversityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UniversityService {

    private final UniversityRepository universityRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public UniversityService(UniversityRepository universityRepository, AdminRepository adminRepository) {
        this.universityRepository = universityRepository;
        this.adminRepository = adminRepository;
    }

    public University addUniversity(UniversityRequest universityRequest) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = auth.getName();

        Admin admin = adminRepository.findByUserEmail(adminEmail);
        if (admin == null) {
            throw new IllegalArgumentException("Authenticated user is not a registered admin");
        }

        University existingUniversity = universityRepository.findByNameAndCreatedByAdmin(universityRequest.getName(), admin);
        if(existingUniversity != null) {
            throw new IllegalArgumentException("A university with the same name already exists for this admin");
        }

        University university = new University();
        university.setName(universityRequest.getName());
        university.setCity(universityRequest.getCity());
        university.setAddress(universityRequest.getAddress());
        university.setCreatedByAdmin(admin);
        universityRepository.save(university);

        return university;
    }
    public List<UniversityResponse> getAllUniversitiesForLoggedInAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = auth.getName();

        Admin admin = adminRepository.findByUserEmail(adminEmail);
        if (admin == null) {
            throw new IllegalArgumentException("Authenticated user is not a registered admin");
        }

        return universityRepository.findAllByCreatedByAdmin(admin)
                .stream()
                .map(university -> new UniversityResponse(university.getName(), university.getCity(), university.getAddress()))
                .collect(Collectors.toList());
    }

}
