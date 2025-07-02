package com.mercadonosso.orders_service.orderservice.adapters.in;


import com.mercadonosso.orders_service.orderservice.adapters.in.dto.CreatingOrderRequest;
import com.mercadonosso.orders_service.orderservice.adapters.in.dto.OrderResponse;
import com.mercadonosso.orders_service.orderservice.core.domain.Order;
import com.mercadonosso.orders_service.orderservice.core.ports.in.OrdersServicePort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrdersServicePort ordersServicePort;

    public OrderController(OrdersServicePort ordersServicePort) {
        this.ordersServicePort = ordersServicePort;
    }

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public OrderResponse createOrder(@RequestBody CreatingOrderRequest request) {
//        Order order = new Order();
//        order.setBuyerId(request.buyerId());
//        order.setStatus(request.status());
//
//        Order createdOrder = ordersServicePort.create(order);
//
//        return toResponse(createdOrder);
//    }
}
