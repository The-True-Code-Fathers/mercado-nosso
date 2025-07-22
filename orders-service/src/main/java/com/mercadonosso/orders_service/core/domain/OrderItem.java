package com.mercadonosso.orders_service.core.domain;

import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {
    
    @NotNull(message = "Listing ID is required")
    private String listingId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
