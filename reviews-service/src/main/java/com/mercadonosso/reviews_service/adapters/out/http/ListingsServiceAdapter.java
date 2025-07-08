package com.mercadonosso.reviews_service.adapters.out.http;

import com.mercadonosso.reviews_service.core.ports.out.ListingServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

public class ListingsServiceAdapter implements ListingServiceClient {
    private final RestTemplate restTemplate;
    private final String listingsServiceUrl;

    public ListingsServiceAdapter(RestTemplate restTemplate,
                                  @Value("${app.services.listings-url}") String listingsServiceUrl) {
        this.restTemplate = restTemplate;
        this.listingsServiceUrl = listingsServiceUrl;
    }

    @Override
    public ListingResponseDTO findListingById(UUID listingId) {
        String url = listingsServiceUrl + "/" + listingId;
        try {
            return restTemplate.getForObject(url, ListingResponseDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }
}
