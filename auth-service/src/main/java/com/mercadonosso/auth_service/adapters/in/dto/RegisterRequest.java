package com.mercadonosso.auth_service.adapters.in.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 255, message = "Full name must be between 2 and 255 characters")
    String fullName,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    String password,
    
    @NotBlank(message = "CPF is required")
    @Size(min = 11, max = 11, message = "CPF must be exactly 11 characters")
    String cpf,
    
    String cnpj, // Optional for sellers
    
    boolean isSeller
) {
}
