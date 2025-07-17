package com.mercadonosso.orders_service.adapters.in.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.mercadonosso.orders_service.core.domain.enums.OrderStatus;

public record OrderResponse(
                UUID orderId,
                UUID buyerId,
                List<String> listingID,
                OrderStatus status,
                LocalDateTime creationTime) {
}
