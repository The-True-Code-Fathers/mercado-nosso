package com.mercadonosso.carts_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadonosso.carts_service.core.ports.in.CartsServicePort;
import com.mercadonosso.carts_service.core.ports.out.CartsRepositoryPort;
import com.mercadonosso.carts_service.core.ports.out.ListingsServicePort;
import com.mercadonosso.carts_service.core.usecases.CartsServiceImpl;

@Configuration
public class BeanConfiguration {

    @Bean
    @Primary
    public CartsServicePort cartServicePort(CartsRepositoryPort cartRepositoryPort,
            ListingsServicePort listingsServicePort, KafkaTemplate<String, String> kafkaTemplate,
            @Value("${topics.cart-clear.name}") String clearCartTopic,
            @Value("${topics.cart-remove.name}") String removeCartTopic, ObjectMapper objectMapper) {
        return new CartsServiceImpl(cartRepositoryPort, listingsServicePort, kafkaTemplate, clearCartTopic,
                removeCartTopic, objectMapper);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}