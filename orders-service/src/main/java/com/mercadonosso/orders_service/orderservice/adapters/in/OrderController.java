package com.mercadonosso.orders_service.orderservice.adapters.in;


import com.mercadonosso.orders_service.orderservice.adapters.in.dto.CreatingOrderRequest;
import com.mercadonosso.orders_service.orderservice.adapters.in.dto.OrderResponse;
import com.mercadonosso.orders_service.orderservice.core.domain.Order;
import com.mercadonosso.orders_service.orderservice.core.domain.enums.OrderStatus;
import com.mercadonosso.orders_service.orderservice.core.ports.in.OrdersServicePort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrdersServicePort ordersServicePort;

    public OrderController(OrdersServicePort ordersServicePort) {
        this.ordersServicePort = ordersServicePort;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody CreatingOrderRequest request) {
        Order order = new Order();

        order.setBuyerId(request.buyerId());
        order.setStatus(request.status());

        Order createdOrder = ordersServicePort.create(order);

        return toResponse(createdOrder);
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getOrderId(),
                order.getBuyerId(),
                order.getListingId(),
                order.getStatus(),
                order.getDate()
        );
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable UUID id) {
        Order orders =  ordersServicePort.findOrderById(id);
        return toResponse(orders);
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = ordersServicePort.findAllOrders();
        return orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable UUID id) {
        Order ordersToDelete = ordersServicePort.findOrderById(id);
        ordersServicePort.delete(ordersToDelete);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}")
    public OrderResponse updateOrder(Order order, OrderStatus status) {
        Order orderToUpdate = ordersServicePort.updateOrder(order, status);
        return toResponse(orderToUpdate);
    }
}
