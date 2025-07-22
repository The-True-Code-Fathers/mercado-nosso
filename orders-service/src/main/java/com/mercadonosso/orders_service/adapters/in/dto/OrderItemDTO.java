package com.mercadonosso.orders_service.adapters.in.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record OrderItemDTO(
        @NotNull(message = "Listing ID is required")
        String listingId,
        
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {
}
