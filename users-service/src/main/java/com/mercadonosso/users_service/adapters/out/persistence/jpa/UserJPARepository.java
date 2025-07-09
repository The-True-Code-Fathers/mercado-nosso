package com.mercadonosso.users_service.adapters.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserJPARepository extends JpaRepository<UserJPAEntity, UUID> {

    Optional<UserJPAEntity> findByEmail(String email);
    Optional<UserJPAEntity> findByEmailAndPassword(String email, String password);
    Optional<UserJPAEntity> findByCpf(String cpf);
    
    boolean existsByEmail(String email);
    
    boolean existsByCpf(String cpf);
}
