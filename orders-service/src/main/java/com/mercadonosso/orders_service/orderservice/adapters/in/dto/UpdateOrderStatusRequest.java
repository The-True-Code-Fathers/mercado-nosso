package com.mercadonosso.orders_service.orderservice.adapters.in.dto;

import com.mercadonosso.orders_service.orderservice.core.domain.enums.OrderStatus;

public class UpdateOrderStatusRequest {
    private OrderStatus status;

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
