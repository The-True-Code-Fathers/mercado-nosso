package com.mercadonosso.carts_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
@EnableDiscoveryClient
public class CartsServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(CartsServiceApplication.class);

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(CartsServiceApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        String port = environment.getProperty("local.server.port", "N/A");
        String appName = environment.getProperty("spring.application.name", "carts-service");

        logger.info("==================================================");
        logger.info("🚀 {} INICIADO COM SUCESSO!", appName.toUpperCase());
        logger.info("🌐 Porta: {}", port);
        logger.info("🔧 Perfil ativo: {}", String.join(",", environment.getActiveProfiles()));
        logger.info("📍 Contexto: {}", environment.getProperty("server.servlet.context-path", "/"));
        logger.info("🎯 Eureka: {}", environment.getProperty("eureka.client.service-url.defaultZone"));
        logger.info("🗄️  MongoDB: {}", environment.getProperty("spring.data.mongodb.uri", "N/A"));
        logger.info("==================================================");
        logger.info("✅ Serviço pronto para receber requisições!");
        logger.info("==================================================");
    }
}
