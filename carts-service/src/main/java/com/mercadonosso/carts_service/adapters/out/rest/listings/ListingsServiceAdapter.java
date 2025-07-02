package com.mercadonosso.carts_service.adapters.out.rest.listings;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.mercadonosso.carts_service.core.ports.out.ListingDetails;
import com.mercadonosso.carts_service.core.ports.out.ListingsServicePort;

@Component
public class ListingsServiceAdapter implements ListingsServicePort {

    private final RestTemplate restTemplate;
    private final String listingsServiceUrl;

    public ListingsServiceAdapter(RestTemplate restTemplate,
            @Value("${app.services.listings-url}") String listingsServiceUrl) {
        this.restTemplate = restTemplate;
        this.listingsServiceUrl = listingsServiceUrl;
    }

    @Override
    public Optional<ListingDetails> findListingsById(UUID listingId) {
        try {
            String url = listingsServiceUrl + "/" + listingId;
            ListingDetails details = restTemplate.getForObject(url, ListingDetails.class);
            return Optional.ofNullable(details);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        }
    }

}
