package com.licenta.aplicatie.controller;

import com.licenta.aplicatie.auth.AuthenticationResponse;
import com.licenta.aplicatie.auth.RegisterRequest;
import com.licenta.aplicatie.models.RoleName;
import com.licenta.aplicatie.models.User;
import com.licenta.aplicatie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/demo-controller")
public class SuperAdminController {

    private final UserService userService;

    @Autowired
    public SuperAdminController(UserService userService) {
        this.userService = userService;
    }

//    @PostMapping("/register-admin")
//    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
//    public ResponseEntity<?> registerAdmin(@RequestBody RegisterRequest registerRequest) {
//        if (!"ADMIN".equalsIgnoreCase(registerRequest.getRoleName())) {
//            return ResponseEntity.badRequest().body("Invalid role. Can only register an admin.");
//        }
//        AuthenticationResponse response = userService.register(registerRequest);
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/superadmin")
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello from secured superadmin endpoint");
    }

    @PutMapping("/lock-admin")
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    public ResponseEntity<?> lockUserAccount(@RequestParam String email) {
        try {
            userService.lockUserAccount(email);
            return ResponseEntity.ok("User account with email " + email + " has been locked successfully");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user found with email: " + email);
        }
    }

    @PutMapping("/unlock-admin")
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    public ResponseEntity<?> unlockUserAccount(@RequestParam String email) {
        try {
            userService.unlockUserAccount(email);
            return ResponseEntity.ok("User account with email " + email + " has been unlocked successfully");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user found with email: " + email);
        }
    }


    @GetMapping("/get-admins")
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    public ResponseEntity<List<User>> getAllAdmins() {
        List<User> admins = userService.getAllUsersByRole(RoleName.ADMIN);
        return ResponseEntity.ok(admins);
    }
}
