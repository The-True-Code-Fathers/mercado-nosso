package com.mercadonosso.orders_service.core.usecases;

import com.mercadonosso.orders_service.core.domain.Orders;
import com.mercadonosso.orders_service.core.domain.enums.OrderStatus;
import com.mercadonosso.orders_service.core.domain.exceptions.BusinessRuleException;
import com.mercadonosso.orders_service.core.domain.exceptions.OrderNotFoundException;
import com.mercadonosso.orders_service.core.ports.out.OrdersRepositoryPort;
import com.mercadonosso.orders_service.core.ports.in.OrdersServicePort;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class OrdersServiceImpl implements OrdersServicePort {

    private final OrdersRepositoryPort ordersRepositoryPort;
    private final Validator validator;

    public OrdersServiceImpl(OrdersRepositoryPort ordersRepositoryPort, Validator validator) {
        this.ordersRepositoryPort = ordersRepositoryPort;
        this.validator = validator;
    }

    public Orders create(Orders order, UUID buyerId, List<UUID> listing) {
        validateOrders(order);
        order.setOrderId(UUID.randomUUID());
        order.setBuyerId(buyerId);
        order.setListingId(listing);
        order.setDate(LocalDateTime.now());
        order.setStatus(OrderStatus.OPEN);
        return ordersRepositoryPort.save(order);
    }


    @Override
    public Orders updateOrder(Orders order, OrderStatus status) {
        order.setStatus(status);
        return ordersRepositoryPort.save(order);
    }

    @Override
    public void delete(Orders order) {
        ordersRepositoryPort.delete(order);
    }

    @Override
    public Orders findOrderById(UUID id) {
        return ordersRepositoryPort.findById(id).orElseThrow(() ->
                new OrderNotFoundException("Order " + id + " não encontrada"));
    }

    @Override
    public List<Orders> findAllOrders() {
        return ordersRepositoryPort.findAll();
    }

    private void validateOrders(Orders order) {
        Set<ConstraintViolation<Orders>> violations = validator.validate(order);
        if (!violations.isEmpty()) {
            throw new BusinessRuleException(violations.iterator().next().getMessage());
        }
    }
}
