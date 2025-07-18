package com.mercadonosso.orders_service.adapters.in.dto;

import java.util.List;
import java.util.UUID;

import com.mercadonosso.orders_service.core.domain.enums.OrderStatus;

public record CreatingOrderRequest(
        UUID orderId,
        UUID buyerId,
        UUID sellerId,
        OrderStatus status,
        List<String> listing) {
}
