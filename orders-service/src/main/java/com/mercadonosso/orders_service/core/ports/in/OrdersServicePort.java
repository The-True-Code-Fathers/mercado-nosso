package com.mercadonosso.orders_service.core.ports.in;

import java.util.List;
import java.util.UUID;

import com.mercadonosso.orders_service.core.domain.Order;
import com.mercadonosso.orders_service.core.domain.enums.OrderStatus;

public interface OrdersServicePort {

    Order create(Order order);
    Order updateOrder(UUID id, OrderStatus status);
    void delete(Order order);
    Order findOrderById(UUID id);
    List<Order> findAllOrders();
    List<Order> findByBuyerId(UUID id);
    List<Order> findBySellerId(UUID sellerId);
}
