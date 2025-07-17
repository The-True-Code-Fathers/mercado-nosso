package com.mercadonosso.orders_service.adapters.out.mongo;

import com.mercadonosso.orders_service.adapters.out.persistence.SpringOrderRepository;
import com.mercadonosso.orders_service.core.domain.Order;
import com.mercadonosso.orders_service.core.ports.out.OrdersRepositoryPort;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderRepositoryAdapter implements OrdersRepositoryPort {

    private final SpringOrderRepository orderRepository;

    public OrderRepositoryAdapter(SpringOrderRepository springOrderRepository) {
        this.orderRepository = springOrderRepository;
    }

    @Override
    public Order save(Order order) {
        OrderModel model = OrderMapper.toModel(order);
        orderRepository.save(model);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        Optional<OrderModel> modelOptional = orderRepository.findById(id);

        return modelOptional.map(OrderMapper::toDomain);
    }

    @Override
    public List<Order> findAll() {
        List<OrderModel> models = orderRepository.findAll();
        return models.stream()
                .map(OrderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByBuyerId(UUID buyerId) {
        List<OrderModel> models = orderRepository.findByBuyerId(buyerId);
        return models.stream()
                .map(OrderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Order order) {
        OrderModel model = OrderMapper.toModel(order);
        orderRepository.delete(model);
    }
}
