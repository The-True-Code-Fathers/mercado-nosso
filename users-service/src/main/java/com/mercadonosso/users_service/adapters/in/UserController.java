package com.mercadonosso.users_service.adapters.in;

import com.mercadonosso.users_service.adapters.in.dto.CreateUserRequest;
import com.mercadonosso.users_service.adapters.in.dto.UpdateUserRequest;
import com.mercadonosso.users_service.adapters.in.dto.UserResponse;
import com.mercadonosso.users_service.core.domain.User;
import com.mercadonosso.users_service.core.ports.in.UserServicePort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

        User userWithNewData = new User();
        userWithNewData.setFullName(request.fullName());
        userWithNewData.setProfilePictureUrl(request.profilePictureUrl()); // Isso aqui converte a entidade pra DTO

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
                user.getListingSellingId(),
                user.getListingBoughtId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.isActive()
        );
    }

    private User toDomain(CreateUserRequest request) {
        User domain = new User();
        domain.setFullName(request.fullName());
        domain.setEmail(request.email());
        domain.setPasswordHash(request.password());
        domain.setCpf(request.cpf());
        domain.setCnpj(request.cnpj());
        domain.setSeller(request.isSeller());
        return domain;
    }
}