package com.mercadonosso.orders_service.core.ports.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mercadonosso.orders_service.core.domain.Order;

public interface OrdersRepositoryPort {

    Order save(Order order);
    Optional<Order> findById(UUID id);
    List<Order> findAll();
    List<Order> findByBuyerId(UUID id);
    List<Order> findBySellerId(UUID sellerId);
    void delete(Order order);

}
