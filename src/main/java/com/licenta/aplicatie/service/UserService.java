package com.licenta.aplicatie.service;

import com.licenta.aplicatie.auth.AuthenticationResponse;
import com.licenta.aplicatie.auth.RegisterRequest;
import com.licenta.aplicatie.models.*;
import com.licenta.aplicatie.repository.AdminRepository;
import com.licenta.aplicatie.repository.RoleRepository;
import com.licenta.aplicatie.repository.SuperAdminRepository;
import com.licenta.aplicatie.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final SuperAdminRepository superAdminRepository;

    public void register(RegisterRequest request) {
        var roleOptional = roleRepository.findByRoleName(RoleName.valueOf(request.getRoleName().toUpperCase()));

        if (!roleOptional.isPresent()) {
            throw new RuntimeException("Role not found: " + request.getRoleName());
        }

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email is already in use: " + request.getEmail());
        }


        var user = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(roleOptional.get())
                .accountLocked(false)
                .build();
        userRepository.save(user);

        if(request.getRoleName().equalsIgnoreCase("ADMIN")) {

            SuperAdmin superAdmin = superAdminRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("No SuperAdmin found"));


            Admin admin = new Admin();
            admin.setId(user.getId());
            admin.setUser(user);
            admin.setCreatedBySuperAdmin(superAdmin);
            adminRepository.save(admin);
        }
    }

    public List<User> getAllUsersByRole(RoleName roleName) {
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
        return userRepository.findAllByRole(role);
    }

    public void lockUserAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + email));
        user.setAccountLocked(true);
        userRepository.save(user);
    }

    public void unlockUserAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        user.setAccountLocked(false);
        userRepository.save(user);
    }
}

