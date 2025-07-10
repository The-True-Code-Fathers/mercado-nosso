package com.mercadonosso.orders_service.config;

import com.mercadonosso.orders_service.adapters.out.mongo.OrderRepositoryAdapter;
import com.mercadonosso.orders_service.core.ports.in.OrdersServicePort;
import com.mercadonosso.orders_service.core.usecases.OrdersServiceImpl;

import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfiguration {

    @Bean
    public OrdersServicePort orderServicePort(
            OrderRepositoryAdapter ordersRepositoryAdapter,
            Validator validator
    ) {
        return new OrdersServiceImpl(ordersRepositoryAdapter, validator);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
