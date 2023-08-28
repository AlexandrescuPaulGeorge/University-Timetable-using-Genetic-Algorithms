package com.licenta.aplicatie.controller;


import com.licenta.aplicatie.auth.AuthenticationRequest;
import com.licenta.aplicatie.auth.AuthenticationResponse;
import com.licenta.aplicatie.auth.RegisterRequest;
import com.licenta.aplicatie.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        service.register(request);
        return ResponseEntity.ok().build();  // Just return HTTP 200 status
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Void> authenticate(@RequestBody AuthenticationRequest request) {
        service.authenticate(request);
        return ResponseEntity.ok().build();  // Just return HTTP 200 status
    }
}
