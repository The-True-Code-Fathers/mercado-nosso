package com.mercadonosso.orders_service.adapters.in;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.mercadonosso.orders_service.adapters.in.dto.DashboardResponse;
import com.mercadonosso.orders_service.core.ports.in.DashboardServicePort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.mercadonosso.orders_service.adapters.in.dto.CreatingOrderRequest;
import com.mercadonosso.orders_service.adapters.in.dto.OrderItemResponse;
import com.mercadonosso.orders_service.adapters.in.dto.OrderResponse;
import com.mercadonosso.orders_service.adapters.in.dto.UpdateOrderStatusRequest;
import com.mercadonosso.orders_service.core.domain.Order;
import com.mercadonosso.orders_service.core.ports.in.OrdersServicePort;

@RestController
public class OrderController {
    private final OrdersServicePort ordersServicePort;
    private final DashboardServicePort dashboardService;

    public OrderController(OrdersServicePort ordersServicePort, 
                          DashboardServicePort dashboardService) {
        this.ordersServicePort = ordersServicePort;
        this.dashboardService = dashboardService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody CreatingOrderRequest request) {
        return toResponse(ordersServicePort.createOrderWithUserUpdates(request));
    }

    private OrderResponse toResponse(Order order) {
        // Converter lista de OrderItem para lista de OrderItemResponse
        List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
                .map(item -> new OrderItemResponse(item.getListingId(), item.getQuantity()))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getOrderId(),
                order.getBuyerId(),
                orderItemResponses,
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
        List<Order> orders = ordersServicePort.findByBuyerId(id);
        return orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/seller/{sellerId}")
    public List<OrderResponse> getBySellerId(@PathVariable UUID sellerId) {
        List<Order> orders = ordersServicePort.findBySellerId(sellerId);
        System.out.println("Found seller orders count: " + orders.size());

        return orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable UUID id) {
        ordersServicePort.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    public OrderResponse updateOrder(@PathVariable UUID id, @RequestBody UpdateOrderStatusRequest request) {
        Order orderToUpdate = ordersServicePort.updateOrder(id, request.getStatus());
        return toResponse(orderToUpdate);
    }

    @GetMapping("/seller/{sellerId}/dashboard")
    public DashboardResponse getDashboard(@PathVariable UUID sellerId,
                                          @RequestParam(required = false) String period) {
        return dashboardService.generateDashboard(sellerId, period);
    }
}
