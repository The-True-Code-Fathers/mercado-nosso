package com.mercadonosso.orders_service.core.ports.in;

import java.util.List;
import java.util.UUID;

import com.mercadonosso.orders_service.core.domain.Order;
import com.mercadonosso.orders_service.core.domain.enums.OrderStatus;
import com.mercadonosso.orders_service.adapters.in.dto.CreatingOrderRequest;

public interface OrdersServicePort {
    Order create(Order order);

    Order createOrderWithUserUpdates(CreatingOrderRequest request);

    Order updateOrder(UUID id, OrderStatus status);

    void delete(UUID orderId);

    Order findOrderById(UUID id);

    List<Order> findAllOrders();

    List<Order> findByBuyerId(UUID id);

    List<Order> findBySellerId(UUID sellerId);
}
