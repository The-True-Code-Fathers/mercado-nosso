package com.mercadonosso.carts_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

import com.mercadonosso.carts_service.core.ports.in.CartsServicePort;
import com.mercadonosso.carts_service.core.ports.out.CartsRepositoryPort;
import com.mercadonosso.carts_service.core.ports.out.ListingsServicePort;
import com.mercadonosso.carts_service.core.usecases.CartsServiceImpl;

@Configuration
public class BeanConfiguration {

    @Bean
    public CartsServicePort cartServicePort(CartsRepositoryPort cartRepositoryPort, ListingsServicePort listingServicePort, KafkaTemplate<String, String> kafkaTemplate, @Value("${topics.cart-clear.name}") String clearCartTopic) {
        return new CartsServiceImpl(cartRepositoryPort, listingServicePort, kafkaTemplate, clearCartTopic);
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}