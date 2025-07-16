package com.mercadonosso.listings_service.adapters.in.web.handler;

import com.mercadonosso.listings_service.adapters.in.web.dto.ErrorResponse;
import com.mercadonosso.listings_service.core.domain.exception.BusinessRuleException;
import com.mercadonosso.listings_service.core.domain.exception.ListingsNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleException(BusinessRuleException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad request",
                ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ListingsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleListingNotFoundException(ListingsNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not found",
                ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.error("Erro de deserialização JSON: {}", ex.getMessage(), ex);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", "Erro ao processar JSON da requisição");
        errorResponse.put("details", ex.getMessage());

        // Log detalhado para identificar o problema específico
        if (ex.getCause() != null) {
            logger.error("Causa raiz do erro: {}", ex.getCause().getMessage(), ex.getCause());
            errorResponse.put("rootCause", ex.getCause().getMessage());
        }

        // Verificar se é erro de UUID especificamente
        if (ex.getMessage().contains("UUID") || ex.getMessage().contains("Can only construct UUIDs")) {
            logger.error("ERRO IDENTIFICADO: Tentativa de converter valor inválido para UUID");
            logger.error(
                    "SOLUÇÃO: O campo sellerId deve ser um UUID válido no formato: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
            logger.error("PROBLEMA: Provavelmente está sendo enviado um ObjectId do MongoDB no lugar de um UUID");

            errorResponse.put("specificError", "UUID_FORMAT_ERROR");
            errorResponse.put("expectedFormat", "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx");
            errorResponse.put("hint", "sellerId deve ser um UUID, não um ObjectId do MongoDB");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        logger.error("Erro de validação: {}", ex.getMessage(), ex);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Validation Error");
        errorResponse.put("message", "Dados da requisição inválidos");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            logger.error("Campo inválido: {} - {}", error.getField(), error.getDefaultMessage());
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });

        errorResponse.put("fieldErrors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        logger.error("Argumento inválido: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Argument",
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                "An unexpected error occurred");
        logger.error("Unexpected error", ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
