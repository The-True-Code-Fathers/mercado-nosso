package com.mercadonosso.users_service.core.ports.in;

import com.mercadonosso.users_service.core.domain.User;
import java.util.Optional;
import java.util.UUID;

public interface UserServicePort {
    
    User createUser(User user);
    
    Optional<User> findById(UUID id);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByCpf(String cpf);
    
    boolean existsByEmail(String email);
    
    boolean existsByCpf(String cpf);
    
    User updateUser(UUID id, User user);
    
    void deleteUser(UUID id);
}
