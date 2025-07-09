package com.mercadonosso.products_service.adapters.in.web.dto;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;

public record CreateProductsRequest(
                @NotBlank(message = "The product SKU can't be null") String sku,
                @NotBlank(message = "The product name can't be null") String name,
                Map<String, Object> specificationsText,
                String brand,
                String category,
                String description) {
}
