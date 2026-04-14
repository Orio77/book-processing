package com.orio.book_processing.auth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.orio.book_processing.auth.dtos.LoginRequest;
import com.orio.book_processing.auth.dtos.LoginResponse;
import com.orio.book_processing.auth.dtos.RegisterRequest;
import com.orio.book_processing.auth.dtos.RegisterResponse;
import com.orio.book_processing.auth.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    public RegisterResponse registerUser(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public LoginResponse loginUser(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

}
