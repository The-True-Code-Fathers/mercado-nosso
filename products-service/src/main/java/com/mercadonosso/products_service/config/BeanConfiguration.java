package com.mercadonosso.products_service.config;

import com.mercadonosso.products_service.adapters.out.persistence.mongo.ProductsRepositoryAdapter;
import com.mercadonosso.products_service.core.ports.in.ProductsServicePort;
import com.mercadonosso.products_service.core.usecases.ProductsServiceImpl;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class BeanConfiguration {
    @Bean
    @Primary
    public ProductsServicePort productsServicePort(
            ProductsRepositoryAdapter productsRepositoryAdapter,
            Validator validator
    ) {
        return new ProductsServiceImpl(productsRepositoryAdapter, validator);
    }
}
