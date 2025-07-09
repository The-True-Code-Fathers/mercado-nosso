package com.mercadonosso.orders_service.adapters.in.dto;

import com.mercadonosso.orders_service.core.domain.enums.OrderStatus;

public class UpdateOrderStatusRequest {
    private OrderStatus status;

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
