package com.orio.book_processing.auth.services;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.orio.book_processing.auth.dtos.LoginRequest;
import com.orio.book_processing.auth.dtos.LoginResponse;
import com.orio.book_processing.auth.dtos.RegisterRequest;
import com.orio.book_processing.auth.dtos.RegisterResponse;
import com.orio.book_processing.auth.models.User;
import com.orio.book_processing.auth.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepo.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("User with the provided email already exists");
        }

        String password = request.getPassword();
        @Nullable
        String encodedPassword = passwordEncoder.encode(password);
        User user = User.builder().email(normalizedEmail).passwordHash(encodedPassword).build();
        User userEntity = userRepo.saveAndFlush(user);

        return new RegisterResponse(userEntity.getId(), userEntity.getEmail());
    }

    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken
                .unauthenticated(normalizedEmail, request.getPassword());

        Authentication authenticationResponse = authManager.authenticate(authentication);

        User user = userRepo.findByEmail(authenticationResponse.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        JwtService.TokenPayload tokenPayload = jwtService.generateToken(user);

        return new LoginResponse(user.getId(), user.getEmail(), "Bearer", tokenPayload.accessToken(),
                tokenPayload.expiresAt());
    }

}
