package com.mercadonosso.reviews_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient 
public class ReviewsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReviewsServiceApplication.class, args);
    }
}
