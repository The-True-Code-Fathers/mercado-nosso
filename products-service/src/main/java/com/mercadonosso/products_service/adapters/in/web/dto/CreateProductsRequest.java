package com.mercadonosso.products_service.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateProductsRequest(
        @NotBlank(message = "The product SKU can't be null") String sku,
        @NotBlank(message = "The product name can't be null") String name,
        String specificationsText,
        String brand,
        String category,
        String description) {
}
