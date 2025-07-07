package com.mercadonosso.auth_service.adapters.out;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.mercadonosso.auth_service.adapters.out.dto.CreateUserRequest;
import com.mercadonosso.auth_service.adapters.out.dto.UserServiceResponse;
import com.mercadonosso.auth_service.core.domain.User;
import com.mercadonosso.auth_service.core.ports.UserRepositoryPort;

@Component
public class UserServiceClient implements UserRepositoryPort {
    
    private final RestTemplate restTemplate;
    private final String usersServiceUrl;
    
    public UserServiceClient(RestTemplate restTemplate,
                           @Value("${services.users.url:http://localhost:8082}") String usersServiceUrl) {
        this.restTemplate = restTemplate;
        this.usersServiceUrl = usersServiceUrl;
    }
    
    @Override
    public User save(User user) {
        CreateUserRequest request = new CreateUserRequest(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getPasswordHash(),
            user.getCpf(),
            user.getCnpj(),
            user.isSeller()
        );
        
        try {
            UserServiceResponse response = restTemplate.postForObject(
                usersServiceUrl + "/users", request, UserServiceResponse.class);
            return mapToUser(response);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user in users-service", e);
        }
    }
    
    @Override
    public Optional<User> findById(UUID id) {
        try {
            UserServiceResponse response = restTemplate.getForObject(
                usersServiceUrl + "/users/{id}", UserServiceResponse.class, id);
            return Optional.ofNullable(response).map(this::mapToUser);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw new RuntimeException("Failed to fetch user by id from users-service", e);
        }
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        try {
            UserServiceResponse response = restTemplate.getForObject(
                usersServiceUrl + "/users/email/{email}", UserServiceResponse.class, email);
            return Optional.ofNullable(response).map(this::mapToUser);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw new RuntimeException("Failed to fetch user by email from users-service", e);
        }
    }
    
    @Override
    public Optional<User> findByCpf(String cpf) {
        try {
            UserServiceResponse response = restTemplate.getForObject(
                usersServiceUrl + "/users/cpf/{cpf}", UserServiceResponse.class, cpf);
            return Optional.ofNullable(response).map(this::mapToUser);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            throw new RuntimeException("Failed to fetch user by CPF from users-service", e);
        }
    }
    
    @Override
    public boolean existsByEmail(String email) {
        try {
            restTemplate.headForHeaders(usersServiceUrl + "/users/email/{email}", email);
            return true;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw new RuntimeException("Failed to check if user exists by email", e);
        }
    }
    
    @Override
    public boolean existsByCpf(String cpf) {
        try {
            restTemplate.headForHeaders(usersServiceUrl + "/users/cpf/{cpf}", cpf);
            return true;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw new RuntimeException("Failed to check if user exists by CPF", e);
        }
    }
    
    @Override
    public void deleteById(UUID id) {
        try {
            restTemplate.delete(usersServiceUrl + "/users/{id}", id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user from users-service", e);
        }
    }
    
    private User mapToUser(UserServiceResponse response) {
        if (response == null) {
            return null;
        }
        
        User user = new User();
        user.setId(response.id());
        user.setFullName(response.fullName());
        user.setEmail(response.email());
        user.setPasswordHash(response.passwordHash());
        user.setCpf(response.cpf());
        user.setCnpj(response.cnpj());
        user.setSeller(response.isSeller());
        user.setProfilePictureUrl(response.profilePictureUrl());
        user.setListingSellingId(response.listingSellingId());
        user.setListingBoughtId(response.listingBoughtId());
        user.setCreatedAt(response.createdAt());
        user.setUpdatedAt(response.updatedAt());
        user.setActive(response.active());
        
        return user;
    }
}
