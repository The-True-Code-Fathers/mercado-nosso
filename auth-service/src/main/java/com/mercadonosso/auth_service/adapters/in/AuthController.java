package com.mercadonosso.auth_service.adapters.in;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mercadonosso.auth_service.adapters.in.dto.AuthRequest;
import com.mercadonosso.auth_service.adapters.in.dto.AuthResponse;
import com.mercadonosso.auth_service.adapters.in.dto.RefreshTokenRequest;
import com.mercadonosso.auth_service.adapters.in.dto.RegisterRequest;
import com.mercadonosso.auth_service.core.ports.AuthServicePort;

import jakarta.validation.Valid;

@RestController
public class AuthController {

    private final AuthServicePort authService;

    public AuthController(AuthServicePort authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse response = authService.login(authRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }
}
