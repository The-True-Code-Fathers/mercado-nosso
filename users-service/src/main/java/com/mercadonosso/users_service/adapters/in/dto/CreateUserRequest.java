package com.mercadonosso.users_service.adapters.in.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "O nome completo é obrigatório.")
        String fullName,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O formato do e-mail é inválido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres.")
        String passwordHash,

        @NotBlank(message = "O CPF é obrigatório.")
        String cpf,

        String cnpj,

        @NotNull(message = "É necessário informar se o usuário é um vendedor.")
        boolean isSeller
) {
}