package com.mercadonosso.reviews_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import com.mercadonosso.reviews_service.adapters.out.http.ListingsServiceAdapter;
import com.mercadonosso.reviews_service.core.ports.in.ReviewsServicePort;
import com.mercadonosso.reviews_service.core.ports.out.ListingServiceClient;
import com.mercadonosso.reviews_service.core.ports.out.ReviewsRepositoryPort;
import com.mercadonosso.reviews_service.core.usecases.ReviewsServiceImpl;

import jakarta.validation.Validator;

@Configuration
public class BeanConfiguration {
    @Bean
    @Primary
    public ReviewsServicePort reviewsServicePort(
            ReviewsRepositoryPort reviewsRepositoryPort,
            Validator validator,
            ListingServiceClient listingServiceClient) {
        return new ReviewsServiceImpl(reviewsRepositoryPort, validator, listingServiceClient);
    }

    @Bean
    public ListingServiceClient listingServiceClient(
            RestTemplate restTemplate,
            @Value("${app.services.listings-url}") String listingsServiceUrl) {
        return new ListingsServiceAdapter(restTemplate, listingsServiceUrl);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}