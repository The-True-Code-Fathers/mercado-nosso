package com.mercadonosso.orders_service.orderservice.config;

import com.mercadonosso.orders_service.orderservice.adapters.out.mongo.OrderRepositoryAdapter;
import com.mercadonosso.orders_service.orderservice.core.ports.in.OrdersServicePort;
import com.mercadonosso.orders_service.orderservice.core.usecases.OrdersServiceImpl;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public OrdersServicePort ordersServicePort(
            OrderRepositoryAdapter ordersRepositoryAdapter,
            Validator validator
    ) {
        return new OrdersServiceImpl(ordersRepositoryAdapter, validator);
    }
}
