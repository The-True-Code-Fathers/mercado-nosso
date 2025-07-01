package com.mercadonosso.orders_service.core.usecases;

import com.mercadonosso.orders_service.core.domain.Orders;
import com.mercadonosso.orders_service.core.domain.enums.OrderStatus;
import com.mercadonosso.orders_service.core.ports.out.OrdersRepositoryPort;
import com.mercadonosso.orders_service.core.ports.in.OrdersServicePort;

import javax.xml.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrdersServiceImpl implements OrdersServicePort {

    private final OrdersRepositoryPort ordersRepositoryPort;
    private final Validator validator;

    public OrdersServiceImpl(OrdersRepositoryPort ordersRepositoryPort, Validator validator) {
        this.ordersRepositoryPort = ordersRepositoryPort;
        this.validator = validator;
    }

    public Orders create(Orders order) {
        order.setDate(LocalDateTime.now());
        order.setStatus(OrderStatus.OPEN);
        return ordersRepositoryPort.save(order);
    }

    @Override
    public Orders updateOrder(Orders order, OrderStatus status) {
        order.setStatus(status);
        return null;
    }

    @Override
    public void delete(Orders order) {
        ordersRepositoryPort.delete(order);
    }

    @Override
    public Orders findOrderById(UUID id) {
        return ordersRepositoryPort.findById(id).orElse(null);
    }

    @Override
    public List<Orders> findAllOrders() {
        return ordersRepositoryPort.findAll();
    }

}
