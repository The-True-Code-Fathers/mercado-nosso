package com.mercadonosso.orders_service.orderservice.adapters.in.dto;

import com.mercadonosso.orders_service.orderservice.core.domain.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse (
        UUID orderId,
        UUID buyerId,
        List<UUID> listingID,
        OrderStatus status,
        LocalDateTime creationTime
){
}
