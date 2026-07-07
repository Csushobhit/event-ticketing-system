package com.event.ticketing.eventticketingsystem.service;

import com.event.ticketing.eventticketingsystem.dto.AuthenticationRequest;
import com.event.ticketing.eventticketingsystem.dto.AuthenticationResponse;
import com.event.ticketing.eventticketingsystem.dto.RegisterRequest;
import com.event.ticketing.eventticketingsystem.model.Role;
import com.event.ticketing.eventticketingsystem.model.User;
import com.event.ticketing.eventticketingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(
            RegisterRequest request
    ) {

        if (
                userRepository.findByEmail(
                        request.getEmail()
                ).isPresent()
        ) {

            throw new IllegalStateException(
                    "Email is already registered."
            );
        }

        User user =
                User.builder()
                        .name(request.getName())
                        .email(request.getEmail())
                        .password(
                                passwordEncoder.encode(
                                        request.getPassword()
                                )
                        )
                        .roles(
                                Set.of(
                                        Role.ROLE_USER
                                )
                        )
                        .build();

        userRepository.save(user);

        String jwtToken =
                jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(
            AuthenticationRequest request
    ) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user =
                userRepository.findByEmail(
                        request.getEmail()
                )
                .orElseThrow(
                        () -> new IllegalStateException(
                                "User not found."
                        )
                );

        String jwtToken =
                jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}