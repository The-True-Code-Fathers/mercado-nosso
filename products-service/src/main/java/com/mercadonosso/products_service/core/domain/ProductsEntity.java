package com.mercadonosso.products_service.core.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductsEntity {
    @NotBlank(message = "Product must havr a SKU.")
    private String sku;

    @NotBlank(message = "Product must have a name.")
    private String name;

    @NotEmpty(message = "Specifications text must not be null.")
    private String specificationsText;

    private String brand;
    private String category;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UUID id;
}
