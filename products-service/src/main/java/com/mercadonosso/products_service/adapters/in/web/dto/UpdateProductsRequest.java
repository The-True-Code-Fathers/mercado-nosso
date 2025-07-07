package com.mercadonosso.products_service.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record UpdateProductsRequest(
        @NotBlank(message = "O SKU é obrigatório")
        String sku,
        @NotBlank(message = "O nome é obrigatório")
        String name,
        String description,
        String brand,
        String category,
        Map<String, Object> specificationsText
) {
}
