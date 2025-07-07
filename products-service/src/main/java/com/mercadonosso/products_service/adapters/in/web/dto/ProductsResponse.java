package com.mercadonosso.products_service.adapters.in.web.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record ProductsResponse(
    UUID id,
    String sku,
    String name,
    Map<String, Object> specificationsText,
    String brand,
    String category,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
