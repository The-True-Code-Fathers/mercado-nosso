package com.mercadonosso.orders_service.adapters.in.dto;

public record OrderItemResponse(
        String listingId,
        Integer quantity
) {
}
