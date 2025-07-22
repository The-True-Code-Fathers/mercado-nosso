package com.mercadonosso.orders_service.adapters.in.dto;

import java.util.List;
import java.util.UUID;

import com.mercadonosso.orders_service.core.domain.ShippingAddress;
import com.mercadonosso.orders_service.core.domain.PaymentMethod;
import com.mercadonosso.orders_service.core.domain.OrderSummary;

public record CreateOrderRequest(
                UUID buyerId,
                UUID sellerId,
                List<OrderItemDTO> orderItems,
                ShippingAddress shippingAddress,
                PaymentMethod paymentMethod,
                OrderSummary orderSummary) {
}
