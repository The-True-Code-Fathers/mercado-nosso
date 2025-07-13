package com.mercadonosso.carts_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String userAgent = request.getHeader("User-Agent");
        String xUserId = request.getHeader("X-User-Id");
        String contentType = request.getContentType();
        String origin = request.getHeader("Origin");

        logger.info("=== REQUISIÇÃO RECEBIDA ===");
        logger.info("Método: {}", method);
        logger.info("URI: {}", uri);
        logger.info("Query String: {}", queryString);
        logger.info("Content-Type: {}", contentType);
        logger.info("Origin: {}", origin);
        logger.info("User-Agent: {}", userAgent);
        logger.info("X-User-Id: {}", xUserId);
        logger.info("Remote Address: {}", request.getRemoteAddr());
        logger.info("Remote Host: {}", request.getRemoteHost());

        // Log de todos os headers
        logger.debug("=== HEADERS ===");
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            logger.debug("Header: {} = {}", headerName, request.getHeader(headerName));
        });

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();

        logger.info("=== RESPOSTA ENVIADA ===");
        logger.info("Método: {} - URI: {} - Status: {}", method, uri, status);

        if (ex != null) {
            logger.error("Exceção durante o processamento da requisição: {} {}", method, uri, ex);
        }
    }
}
