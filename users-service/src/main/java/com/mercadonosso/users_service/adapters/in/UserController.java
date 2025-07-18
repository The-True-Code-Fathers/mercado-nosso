package com.mercadonosso.users_service.adapters.in;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mercadonosso.users_service.adapters.in.dto.CreateUserRequest;
import com.mercadonosso.users_service.adapters.in.dto.LoginRequest;
import com.mercadonosso.users_service.adapters.in.dto.UpdateUserRequest;
import com.mercadonosso.users_service.adapters.in.dto.UserResponse;
import com.mercadonosso.users_service.core.domain.User;
import com.mercadonosso.users_service.core.ports.in.UserServicePort;

import jakarta.validation.Valid;

@RestController
public class UserController {
    private final UserServicePort userService;

    public UserController(UserServicePort userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        User userToCreate = toDomain(request);
        User createdUser = userService.createUser(userToCreate);
        return toResponse(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return userService.findById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> findByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<UserResponse> findByCpf(@PathVariable String cpf) {
        return userService.findByCpf(cpf)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@RequestHeader("X-User-Id") UUID userId) {
        return userService.findById(userId)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> patchCurrentUser(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody UpdateUserRequest request) {

        System.out.println("=== BACKEND DEBUG CEP ===");
        System.out.println("User ID: " + userId);
        System.out.println("Request completo: " + request);
        System.out.println("CEP recebido: " + request.cep());
        System.out.println("CEP é null?: " + (request.cep() == null));
        System.out.println("CEP está vazio?: " + (request.cep() != null && request.cep().isEmpty()));
        System.out.println("========================");

        User user = userService.findById(userId).
                orElseThrow(() -> new RuntimeException("User not found"));

        User userWithNewData = new User();
        userWithNewData.setFullName(request.fullName());
        userWithNewData.setEmail(request.email());
        userWithNewData.setTelephoneNumber(request.telephoneNumber());
        userWithNewData.setProfilePictureUrl(request.profilePictureUrl()); // Isso aqui converte a entidade pra DTO
        userWithNewData.setCep(request.cep());

        System.out.println("=== DOMAIN OBJECT DEBUG ===");
        System.out.println("CEP no domain object: " + userWithNewData.getCep());
        System.out.println("=============================");

        if (request.cnpj() != null) {
            userWithNewData.setCnpj(request.cnpj());
        }

        if (request.socialReason() != null) {
            userWithNewData.setSocialReason(request.socialReason());
        }

        if (request.isSeller()) {
            System.out.println("False -> true");
            userWithNewData.setSeller(true);
        } else {
            System.out.println("Nao atualizou, ficou como false mesmo");
        }

        if (request.orderSellingId() != null && !request.orderSellingId().isEmpty()) {
            if (user.getOrderSellingId() == null) user.setOrderSellingId(new ArrayList<>());
            for (UUID id : request.orderSellingId()) {
                if (!user.getOrderSellingId().contains(id)) {
                    user.getOrderSellingId().add(id);
                }
            }
        }

        if (request.orderBoughtId() != null && !request.orderBoughtId().isEmpty()) {
            if (user.getOrderBoughtId() == null) user.setOrderBoughtId(new ArrayList<>());
            for (UUID id : request.orderBoughtId()) {
                if (!user.getOrderBoughtId().contains(id)) {
                    user.getOrderBoughtId().add(id);
                }
            }
        }

        User updatedUser = userService.updateUser(userId, userWithNewData);

        return ResponseEntity.ok(toResponse(updatedUser));
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inactivateCurrentUser(@RequestHeader("X-User-Id") UUID userId) {
        userService.deleteUser(userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.isSeller(),
                user.getProfilePictureUrl(),
                user.getOrderSellingId(),
                user.getOrderBoughtId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.isActive(),
                user.getCep(),
                user.getTelephoneNumber(),
                user.getSocialReason());

    }

    private User toDomain(CreateUserRequest request) {
        User domain = new User();
        domain.setFullName(request.fullName());
        domain.setEmail(request.email());
        domain.setPasswordHash(request.passwordHash());
        domain.setCpf(request.cpf());
        domain.setCnpj(request.cnpj());
        domain.setSeller(request.isSeller());
        return domain;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request) {
        User authenticatedUser = userService.login(request.email(), request.passwordHash());

        UserResponse response = new UserResponse(
                authenticatedUser.getId(),
                authenticatedUser.getFullName(),
                authenticatedUser.getEmail(),
                authenticatedUser.isSeller(),
                authenticatedUser.getProfilePictureUrl(),
                authenticatedUser.getOrderSellingId(),
                authenticatedUser.getOrderBoughtId(),
                authenticatedUser.getCreatedAt(),
                authenticatedUser.getUpdatedAt(),
                authenticatedUser.isActive(),
                authenticatedUser.getCep(),
                authenticatedUser.getTelephoneNumber(),
                authenticatedUser.getSocialReason());

        return ResponseEntity.ok(response);
    }




}