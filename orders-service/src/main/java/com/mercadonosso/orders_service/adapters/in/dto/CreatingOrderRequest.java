package com.mercadonosso.orders_service.adapters.in.dto.create_order;

import java.util.List;
import java.util.UUID;

import com.mercadonosso.orders_service.core.domain.enums.OrderStatus;
import com.mercadonosso.orders_service.core.domain.ShippingAddress;
import com.mercadonosso.orders_service.core.domain.PaymentMethod;
import com.mercadonosso.orders_service.core.domain.OrderSummary;

public record CreatingOrderRequest(
                UUID orderId,
                UUID buyerId,
                UUID sellerId,
                OrderStatus status,
                List<String> listing,
                ShippingAddress shippingAddress,
                PaymentMethod paymentMethod,
                OrderSummary orderSummary) {
}
