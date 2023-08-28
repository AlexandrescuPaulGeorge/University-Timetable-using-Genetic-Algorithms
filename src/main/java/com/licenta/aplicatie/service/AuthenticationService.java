package com.licenta.aplicatie.service;


import com.licenta.aplicatie.auth.AuthenticationRequest;
import com.licenta.aplicatie.auth.AuthenticationResponse;
import com.licenta.aplicatie.auth.RegisterRequest;
import com.licenta.aplicatie.models.RoleName;
import com.licenta.aplicatie.models.User;
import com.licenta.aplicatie.repository.RoleRepository;
import com.licenta.aplicatie.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    public void register(RegisterRequest request) {

        var roleOptional = roleRepository.findByRoleName(RoleName.valueOf(request.getRoleName().toUpperCase()));

        if (!roleOptional.isPresent()) {
            throw new RuntimeException("Role not found: " + request.getRoleName());
        }

        var user = User.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(roleOptional.get())
                .build();
        repository.save(user);
    }

    public void authenticate(AuthenticationRequest request) {

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDetails.getUsername(),
                        request.getPassword(),
                        userDetails.getAuthorities()
                )
        );
    }
}
