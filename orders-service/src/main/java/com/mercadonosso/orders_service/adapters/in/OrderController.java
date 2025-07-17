package com.mercadonosso.orders_service.adapters.in;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class OrderController {
    private final OrdersServicePort ordersServicePort;

    public OrderController(OrdersServicePort ordersServicePort) {
        this.ordersServicePort = ordersServicePort;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody CreatingOrderRequest request) {
        System.out.println("=== DEBUG CREATE ORDER ===");
        System.out.println("Request listing: " + request.listing());
        System.out.println("Request listing class: "
                + (request.listing() != null ? request.listing().getClass().getName() : "null"));
        if (request.listing() != null && !request.listing().isEmpty()) {
            System.out.println("First listing: " + request.listing().get(0));
            System.out.println("First listing class: " + request.listing().get(0).getClass().getName());
        }

        Order order = new Order();

        order.setOrderId(request.orderId());
        order.setListingId(request.listing());
        order.setBuyerId(request.buyerId());
        order.setStatus(request.status());

        Order createdOrder = ordersServicePort.create(order);

        return toResponse(createdOrder);
    }

    private OrderResponse toResponse(Order order) {
        System.out.println("=== DEBUG TO RESPONSE ===");
        System.out.println("Order listingId: " + order.getListingId());
        System.out.println("Order listingId class: "
                + (order.getListingId() != null ? order.getListingId().getClass().getName() : "null"));
        if (order.getListingId() != null && !order.getListingId().isEmpty()) {
            System.out.println("First order listingId: " + order.getListingId().get(0));
            System.out.println("First order listingId class: " + order.getListingId().get(0).getClass().getName());
        }

        return new OrderResponse(
                order.getOrderId(),
                order.getBuyerId(),
                order.getListingId(),
                order.getStatus(),
                order.getDate());
    }

    @GetMapping("{id}")
    public OrderResponse getOrderById(@PathVariable UUID id) {
        Order orders = ordersServicePort.findOrderById(id);
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
        System.out.println("=== DEBUG GET BY BUYER ID ===");
        System.out.println("Buyer ID: " + id);

        List<Order> orders = ordersServicePort.findByBuyerId(id);
        System.out.println("Found orders count: " + orders.size());

        return orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/seller/{sellerId}")
    public List<OrderResponse> getBySellerId(@PathVariable UUID sellerId) {
        System.out.println("=== DEBUG GET BY SELLER ID ===");
        System.out.println("Seller ID: " + sellerId);

        List<Order> orders = ordersServicePort.findBySellerId(sellerId);
        System.out.println("Found seller orders count: " + orders.size());

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
