package com.mercadonosso.auth_service.core.ports;

import com.mercadonosso.auth_service.adapters.in.dto.AuthRequest;
import com.mercadonosso.auth_service.adapters.in.dto.AuthResponse;
import com.mercadonosso.auth_service.adapters.in.dto.RefreshTokenRequest;
import com.mercadonosso.auth_service.adapters.in.dto.RegisterRequest;

public interface AuthServicePort {
    
    AuthResponse register(RegisterRequest request);
    
    AuthResponse login(AuthRequest request);
    
    AuthResponse refreshToken(RefreshTokenRequest request);
}
