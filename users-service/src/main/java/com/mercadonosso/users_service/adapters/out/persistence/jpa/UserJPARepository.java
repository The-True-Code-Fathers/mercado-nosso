package com.mercadonosso.users_service.adapters.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJPARepository extends JpaRepository<UserJPAEntity, Long> {

    Optional<UserJPAEntity> findByEmail(String email);
}
