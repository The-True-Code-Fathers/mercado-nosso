package com.mercadonosso.auth_service.core.ports;

import com.mercadonosso.auth_service.core.domain.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    
    User save(User user);
    
    Optional<User> findById(UUID id);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByCpf(String cpf);
    
    boolean existsByEmail(String email);
    
    boolean existsByCpf(String cpf);
    
    void deleteById(UUID id);
}
