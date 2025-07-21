package com.mercadonosso.orders_service.core.usecases;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.mercadonosso.orders_service.adapters.in.dto.CreatingOrderRequest;
import com.mercadonosso.orders_service.adapters.out.rest.users.UsersServiceAdapter;
import com.mercadonosso.orders_service.core.domain.Order;
import com.mercadonosso.orders_service.core.domain.enums.OrderStatus;
import com.mercadonosso.orders_service.core.domain.exceptions.BusinessRuleException;
import com.mercadonosso.orders_service.core.domain.exceptions.OrderNotFoundException;
import com.mercadonosso.orders_service.core.ports.in.OrdersServicePort;
import com.mercadonosso.orders_service.core.ports.out.OrdersRepositoryPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrdersServiceImpl implements OrdersServicePort {
    private static final Logger logger = LoggerFactory.getLogger(OrdersServiceImpl.class);

    private final OrdersRepositoryPort ordersRepositoryPort;
    private final Validator validator;
    private final UsersServiceAdapter usersServiceAdapter;

    public OrdersServiceImpl(OrdersRepositoryPort ordersRepositoryPort, Validator validator,
            UsersServiceAdapter usersServiceAdapter) {
        this.ordersRepositoryPort = ordersRepositoryPort;
        this.validator = validator;
        this.usersServiceAdapter = usersServiceAdapter;
    }

    public Order create(Order order) {
        validateOrders(order);
        order.setOrderId(UUID.randomUUID());
        order.setDate(LocalDateTime.now());
        order.setStatus(OrderStatus.OPEN);
        return ordersRepositoryPort.save(order);
    }

    @Override
    public Order createOrderWithUserUpdates(CreatingOrderRequest request) {
        logger.info("Creating order with user updates for buyer: {} and seller: {}",
                request.buyerId(), request.sellerId());

        // Map DTO to domain object
        Order order = mapRequestToOrder(request);

        // Create the order
        Order createdOrder = create(order);

        // Update user records
        try {
            usersServiceAdapter.addOrderToBuyer(createdOrder.getBuyerId(), createdOrder.getOrderId());
            usersServiceAdapter.addOrderToSeller(createdOrder.getSellerId(), createdOrder.getOrderId());

            logger.info("Successfully created order {} and updated user records", createdOrder.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to update user records for order {}: {}", createdOrder.getOrderId(), e.getMessage());
            // Aqui você pode decidir se quer reverter a criação da order ou não
            // Por agora, vamos deixar a order criada mesmo se falhar a atualização dos
            // usuários
            throw new BusinessRuleException("Order created but failed to update user records: " + e.getMessage());
        }

        return createdOrder;
    }

    private Order mapRequestToOrder(CreatingOrderRequest request) {
        Order order = new Order();
        order.setOrderId(request.orderId());
        order.setListingId(request.listingsId());
        order.setBuyerId(request.buyerId());
        order.setStatus(request.status());
        order.setSellerId(request.sellerId());
        order.setShippingAddress(request.shippingAddress());
        order.setPaymentMethod(request.paymentMethod());
        order.setOrderSummary(request.orderSummary());
        return order;
    }

    @Override
    public Order updateOrder(UUID id, OrderStatus status) {
        Order order = findOrderById(id);
        order.setStatus(status);
        return ordersRepositoryPort.save(order);
    }

    @Override
    public void delete(UUID orderId) {
        logger.info("Inactivating (soft delete) order with ID: {}", orderId);

        Order order = findOrderById(orderId);
        order.setStatus(OrderStatus.CANCELLED);
        ordersRepositoryPort.save(order);

        logger.info("Order {} successfully inactivated", orderId);
    }

    @Override
    public Order findOrderById(UUID id) {
        return ordersRepositoryPort.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order " + id + " não encontrada"));
    }

    @Override
    public List<Order> findAllOrders() {
        return ordersRepositoryPort.findAll();
    }

    @Override
    public List<Order> findByBuyerId(UUID id) {
        return ordersRepositoryPort.findByBuyerId(id);
    }

    @Override
    public List<Order> findBySellerId(UUID sellerId) {
        return ordersRepositoryPort.findBySellerId(sellerId);
    }

    private void validateOrders(Order order) {
        Set<ConstraintViolation<Order>> violations = validator.validate(order);
        if (!violations.isEmpty()) {
            throw new BusinessRuleException(violations.iterator().next().getMessage());
        }
    }
}
