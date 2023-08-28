package com.licenta.aplicatie.controller;


import com.licenta.aplicatie.auth.UniversityRequest;
import com.licenta.aplicatie.models.University;
import com.licenta.aplicatie.service.UniversityService;
import com.licenta.aplicatie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final UserService userService;

    private final UniversityService universityService;

    @Autowired
    public AdminController(UserService userService, UniversityService universityService) {
        this.userService = userService;
        this.universityService = universityService;
    }
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello from secured admin endpoint");
    }

}
