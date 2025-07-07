package com.mercadonosso.users_service.adapters.in;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mercadonosso.users_service.adapters.in.dto.UpdateUserRequest;
import com.mercadonosso.users_service.core.domain.User;
import com.mercadonosso.users_service.core.ports.in.UserServicePort;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserServicePort userService;

    public UserController(UserServicePort userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable UUID id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> findByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<User> findByCpf(@PathVariable String cpf) {
        return userService.findByCpf(cpf)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("X-User-Id") UUID userId) {
        return userService.findById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/me")
    public ResponseEntity<User> patchCurrentUser(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody UpdateUserRequest request) {
        
        return userService.findById(userId)
                .map(user -> {
                    // Update only the fields that are provided (PATCH)
                    if (request.fullName() != null) {
                        user.setFullName(request.fullName());
                    }
                    if (request.profilePictureUrl() != null) {
                        user.setProfilePictureUrl(request.profilePictureUrl());
                    }
                    
                    User updatedUser = userService.updateUser(userId, user);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inactivateUser(@RequestHeader("X-User-Id") UUID userId) {
        userService.findById(userId)
                .ifPresent(user -> {
                    user.setActive(false);
                    userService.updateUser(userId, user);
                });
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }
}
