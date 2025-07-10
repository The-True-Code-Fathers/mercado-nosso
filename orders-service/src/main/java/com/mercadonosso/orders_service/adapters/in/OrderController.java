package com.mercadonosso.orders_service.adapters.in;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mercadonosso.orders_service.adapters.in.dto.CreatingOrderRequest;
import com.mercadonosso.orders_service.adapters.in.dto.OrderResponse;
import com.mercadonosso.orders_service.adapters.in.dto.UpdateOrderStatusRequest;
import com.mercadonosso.orders_service.core.domain.Order;
import com.mercadonosso.orders_service.core.ports.in.OrdersServicePort;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {
    private final OrdersServicePort ordersServicePort;

    public OrderController(OrdersServicePort ordersServicePort) {
        this.ordersServicePort = ordersServicePort;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody CreatingOrderRequest request) {
        Order order = new Order();

        order.setOrderId(request.orderId());
        order.setListingId(request.listing());
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

    @GetMapping("{id}")
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

    @GetMapping("/all/{id}")
    public List<OrderResponse> getByBuyerId(@PathVariable UUID id) {
        List<Order> orders = ordersServicePort.findByBuyerId(id);
        return orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable UUID id) {
        Order ordersToDelete = ordersServicePort.findOrderById(id);
        ordersServicePort.delete(ordersToDelete);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    public OrderResponse updateOrder(@PathVariable UUID id, @RequestBody UpdateOrderStatusRequest request) {
        Order orderToUpdate = ordersServicePort.updateOrder(id, request.getStatus());
        return toResponse(orderToUpdate);
    }
}
