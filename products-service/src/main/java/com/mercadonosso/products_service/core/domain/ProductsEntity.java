package com.mercadonosso.products_service.core.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductsEntity {
    @NotNull(message = "Product must have a Id.")
    private UUID id;

    @NotBlank(message = "Product must havr a SKU.")
    private String sku;

    @NotBlank(message = "Product must have a name.")
    private String name;

    @NotBlank(message = "Product must have a specification")
    private String specificationsText;

    private String brand;
    private String category;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
