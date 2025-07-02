package com.mercadonosso.orders_service.orderservice.adapters.out.mongo;

import com.mercadonosso.orders_service.orderservice.adapters.out.persistence.SpringOrderRepository;
import com.mercadonosso.orders_service.orderservice.core.domain.Order;
import com.mercadonosso.orders_service.orderservice.core.ports.out.OrdersRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderRepositoryAdapter implements OrdersRepositoryPort {

    private final SpringOrderRepository mongoRepository;


    public OrderRepositoryAdapter(SpringOrderRepository springOrderRepository) {
        this.mongoRepository = springOrderRepository;
    }

    @Override
    public Order save(Order order) {
        OrderModel model = OrderMapper.toModel(order);
        mongoRepository.save(model);
        return order;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        Optional<OrderModel> modelOptional = mongoRepository.findById(id.toString());

        return modelOptional.map(OrderMapper::toDomain);
    }

    @Override
    public List<Order> findAll() {
        List<OrderModel> models = mongoRepository.findAll();
        return models.stream()
                .map(OrderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Order order) {
        OrderModel model = OrderMapper.toModel(order);
        mongoRepository.delete(model);
    }
}
