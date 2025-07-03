package com.mercadonosso.users_service.adapters.in;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mercadonosso.users_service.core.ports.in.UserServicePort;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServicePort userService;

    public UserController(UserServicePort userService) {
        this.userService = userService;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser() {

    }

    @PostMapping("/me")
    public void getCurrentUser() {

    }

    @PatchMapping("/me")
    public void pathCurrentUser() {

    }

    @DeleteMapping("/me")
    public void inactivateUser() {

    }

    @GetMapping("/:id")
    public void getUser() {

    }
}
