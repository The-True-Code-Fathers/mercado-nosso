package com.mercadonosso.orders_service.core.ports.in;

import com.mercadonosso.orders_service.core.domain.Orders;
import com.mercadonosso.orders_service.core.domain.enums.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrdersServicePort {

    Orders create(Orders order, UUID buyerId, List<UUID> listings);
    Orders updateOrder(Orders order, OrderStatus status);
    void delete(Orders order);
    Orders findOrderById(UUID id);
    List<Orders> findAllOrders();

}
