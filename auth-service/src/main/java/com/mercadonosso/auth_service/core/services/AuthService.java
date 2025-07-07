package com.mercadonosso.auth_service.core.services;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mercadonosso.auth_service.adapters.in.dto.AuthRequest;
import com.mercadonosso.auth_service.adapters.in.dto.AuthResponse;
import com.mercadonosso.auth_service.adapters.in.dto.RefreshTokenRequest;
import com.mercadonosso.auth_service.adapters.in.dto.RegisterRequest;
import com.mercadonosso.auth_service.core.domain.User;
import com.mercadonosso.auth_service.core.exceptions.InvalidCredentialsException;
import com.mercadonosso.auth_service.core.exceptions.UserAlreadyExistsException;
import com.mercadonosso.auth_service.core.exceptions.InvalidTokenException;
import com.mercadonosso.auth_service.core.ports.AuthServicePort;
import com.mercadonosso.auth_service.core.ports.UserRepositoryPort;

@Service
public class AuthService implements AuthServicePort {
    
    private final UserRepositoryPort userRepository;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    
    public AuthService(UserRepositoryPort userRepository,
                      JwtTokenService jwtTokenService,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }
        
        if (userRepository.existsByCpf(request.cpf())) {
            throw new UserAlreadyExistsException("User with this CPF already exists");
        }
        
        // Validate CNPJ if user is a seller
        if (request.isSeller() && (request.cnpj() == null || request.cnpj().trim().isEmpty())) {
            throw new IllegalArgumentException("CNPJ is required for sellers");
        }
        
        // Create new user
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setCpf(request.cpf());
        user.setCnpj(request.cnpj());
        user.setSeller(request.isSeller());
        
        User savedUser = userRepository.save(user);
        
        // Generate tokens
        String accessToken = jwtTokenService.generateAccessToken(
            savedUser.getId(), savedUser.getEmail(), savedUser.isSeller());
        String refreshToken = jwtTokenService.generateRefreshToken(savedUser.getId());
        
        return new AuthResponse(accessToken, refreshToken, savedUser.getId());
    }
    
    @Override
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        
        if (!user.isActive()) {
            throw new InvalidCredentialsException("Account is deactivated");
        }
        
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        
        // Generate tokens
        String accessToken = jwtTokenService.generateAccessToken(
            user.getId(), user.getEmail(), user.isSeller());
        String refreshToken = jwtTokenService.generateRefreshToken(user.getId());
        
        return new AuthResponse(accessToken, refreshToken, user.getId());
    }
    
    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        try {
            // Validate refresh token
            UUID userId = jwtTokenService.getUserIdFromToken(request.refreshToken());
            
            // Check if token is expired
            if (jwtTokenService.isTokenExpired(request.refreshToken())) {
                throw new InvalidTokenException("Refresh token has expired");
            }
            
            // Get user details
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidTokenException("User not found"));
            
            if (!user.isActive()) {
                throw new InvalidTokenException("Account is deactivated");
            }
            
            // Generate new tokens
            String newAccessToken = jwtTokenService.generateAccessToken(
                user.getId(), user.getEmail(), user.isSeller());
            String newRefreshToken = jwtTokenService.generateRefreshToken(user.getId());
            
            return new AuthResponse(newAccessToken, newRefreshToken, user.getId());
            
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid refresh token");
        }
    }
}
