package com.mercadonosso.orders_service.orderservice.adapters.in.dto;

import com.mercadonosso.orders_service.orderservice.core.domain.enums.OrderStatus;


import java.util.List;
import java.util.UUID;

public record CreatingOrderRequest(UUID orderId,
                                   UUID buyerId,
                                   OrderStatus status,
                                   List<UUID> listing
                                   ){
}
