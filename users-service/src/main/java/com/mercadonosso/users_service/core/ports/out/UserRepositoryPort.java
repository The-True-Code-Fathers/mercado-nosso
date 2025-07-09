package com.mercadonosso.users_service.core.ports.out;

import com.mercadonosso.users_service.core.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findByCpf(String cpf);
    Optional<User> findByEmailAndPassword(String email, String password);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
    void deleteById(UUID id);
}
