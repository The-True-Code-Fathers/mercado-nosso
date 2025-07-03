package com.mercadonosso.users_service.adapters.out.persistence.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.mercadonosso.users_service.core.domain.User;
import com.mercadonosso.users_service.core.ports.out.UserRepositoryPort;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJPARepository jpaRepository;

    private UserRepositoryAdapter(final UserJPARepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public void deleteById(UUID id) {

    }
}
