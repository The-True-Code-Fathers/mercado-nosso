package com.mercadonosso.seller_daily_analytics_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient 
public class SellerDailyAnalyticsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SellerDailyAnalyticsServiceApplication.class, args);
    }
}
