package com.mercadonosso.orders_service.adapters.out.rest.users;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Component
public class UsersServiceAdapter {
    private final RestTemplate restTemplate;
    @Value("${app.services.users-url}")
    private String usersUrl;

    public UsersServiceAdapter(RestTemplate restTemplate, @Value("${app.services.users-url}") String usersUrl) {
        this.restTemplate = restTemplate;
        this.usersUrl = usersUrl;
    }

    public void addOrderToSeller(UUID sellerId, UUID orderId) {
        UpdateUserRequest sellerUpdate = new UpdateUserRequest(
                null, null, null, null, null, null, null, true,
                List.of(orderId), null
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", sellerId.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UpdateUserRequest> entity = new HttpEntity<>(sellerUpdate, headers);
        restTemplate.exchange(usersUrl + "/me", HttpMethod.PATCH, entity, Void.class);
    }

    public void addOrderToBuyer(UUID buyerId, UUID orderId) {
        UpdateUserRequest buyerUpdate = new UpdateUserRequest(
                null, null, null, null, null, null, null, false,
                null, List.of(orderId)
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", buyerId.toString());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UpdateUserRequest> entity = new HttpEntity<>(buyerUpdate, headers);
        restTemplate.exchange(usersUrl + "/me", HttpMethod.PATCH, entity, Void.class);
    }
}