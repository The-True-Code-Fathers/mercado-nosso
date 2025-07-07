package com.mercadonosso.users_service.adapters.out.persistence.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.mercadonosso.users_service.core.domain.User;
import com.mercadonosso.users_service.core.ports.out.UserRepositoryPort;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJPARepository jpaRepository;

    public UserRepositoryAdapter(final UserJPARepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserJPAEntity entity = UserMapper.toJPAEntity(user);
        UserJPAEntity savedEntity = jpaRepository.save(entity);
        return UserMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByCpf(String cpf) {
        return jpaRepository.findByCpf(cpf)
                .map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return jpaRepository.existsByCpf(cpf);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
