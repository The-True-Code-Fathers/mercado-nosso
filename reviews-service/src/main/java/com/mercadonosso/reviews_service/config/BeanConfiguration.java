package com.mercadonosso.reviews_service.config;

import com.mercadonosso.reviews_service.adapters.out.persistence.mongo.ReviewsRepositoryAdapter;
import com.mercadonosso.reviews_service.core.ports.in.ReviewsServicePort;
import com.mercadonosso.reviews_service.core.usecases.ReviewsServiceImpl;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import jakarta.validation.Validator;

@Configuration
public class BeanConfiguration {
    @Bean
    @Primary
    public ReviewsServicePort reviewsServicePort(
            ReviewsRepositoryAdapter reviewsRepositoryAdapter,
            Validator validator
    ) {
        return new ReviewsServiceImpl(reviewsRepositoryAdapter, validator);
    }


}
