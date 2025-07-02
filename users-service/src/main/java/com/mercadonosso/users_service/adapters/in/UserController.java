package com.mercadonosso.users_service.adapters.in;

import com.mercadonosso.users_service.core.ports.in.UserServicePort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
