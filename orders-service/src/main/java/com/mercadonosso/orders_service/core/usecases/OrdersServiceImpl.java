package com.mercadonosso.orders_service.core.usecases;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.mercadonosso.orders_service.core.domain.Order;
import com.mercadonosso.orders_service.core.domain.enums.OrderStatus;
import com.mercadonosso.orders_service.core.domain.exceptions.BusinessRuleException;
import com.mercadonosso.orders_service.core.domain.exceptions.OrderNotFoundException;
import com.mercadonosso.orders_service.core.ports.in.OrdersServicePort;
import com.mercadonosso.orders_service.core.ports.out.OrdersRepositoryPort;

public class OrdersServiceImpl implements OrdersServicePort {

    private final OrdersRepositoryPort ordersRepositoryPort;
    private final Validator validator;

    public OrdersServiceImpl(OrdersRepositoryPort ordersRepositoryPort, Validator validator) {
        this.ordersRepositoryPort = ordersRepositoryPort;
        this.validator = validator;
    }

    public Order create(Order order) {
        validateOrders(order);
        order.setOrderId(UUID.randomUUID());
        order.setBuyerId(order.getBuyerId());
        order.setListingId(order.getListingId());
        order.setDate(LocalDateTime.now());
        order.setStatus(OrderStatus.OPEN);
        return ordersRepositoryPort.save(order);
    }


    @Override
    public Order updateOrder(UUID id, OrderStatus status) {
        Order order = findOrderById(id);
        order.setStatus(status);
        return ordersRepositoryPort.save(order);
    }

    @Override
    public void delete(Order order) {
        ordersRepositoryPort.delete(order);
    }

    @Override
    public Order findOrderById(UUID id) {
        return ordersRepositoryPort.findById(id).orElseThrow(() ->
                new OrderNotFoundException("Order " + id + " não encontrada"));
    }

    @Override
    public List<Order> findAllOrders() {
        return ordersRepositoryPort.findAll();
    }

    @Override
    public List<Order> findByBuyerId(UUID id) {
        return ordersRepositoryPort.findByBuyerId(id);
    }

    private void validateOrders(Order order) {
        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        if (!violations.isEmpty()) {
            throw new BusinessRuleException(violations.iterator().next().getMessage());
        }
    }
}
