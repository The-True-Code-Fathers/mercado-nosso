package com.mercadonosso.products_service.adapters.in.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductsResponse(
    UUID id,
    String sku,
    String name,
    String specificationsText,
    String brand,
    String category,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
