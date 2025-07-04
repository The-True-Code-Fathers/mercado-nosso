package com.mercadonosso.listings_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.mercadonosso.listings_service.adapters.out.persistence.mongo.ListingsRepositoryAdapter;
import com.mercadonosso.listings_service.core.ports.in.ListingsServicePort;
import com.mercadonosso.listings_service.core.usecases.ListingsServiceImpl;

import jakarta.validation.Validator;

@Configuration
public class BeanConfiguration {

    @Bean
    @Primary
    public ListingsServicePort listingServicePort(
            ListingsRepositoryAdapter listingRepositoryAdapter,
            Validator validator) {
        return new ListingsServiceImpl(listingRepositoryAdapter, validator);
    }
}
