package com.mercadonosso.users_service.core.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mercadonosso.users_service.core.domain.User;
import com.mercadonosso.users_service.core.exceptions.UserNotFoundException;
import com.mercadonosso.users_service.core.ports.in.UserServicePort;
import com.mercadonosso.users_service.core.ports.out.UserRepositoryPort;

@Service
public class UserService implements UserServicePort {
    
    private final UserRepositoryPort userRepository;
    
    public UserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public User createUser(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        return userRepository.save(user);
    }
    
    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public Optional<User> findByCpf(String cpf) {
        return userRepository.findByCpf(cpf);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public boolean existsByCpf(String cpf) {
        return userRepository.existsByCpf(cpf);
    }
    
    @Override
    public User updateUser(UUID id, User user) {
        user.setId(id);
        return userRepository.save(user);
    }
    
    @Override
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        
        // Check if user is already inactive
        if (!user.isActive()) {
            throw new IllegalStateException("User is already deactivated");
        }
        
        // Soft delete - set active to false
        user.setActive(false);
        userRepository.save(user);
    }
}
