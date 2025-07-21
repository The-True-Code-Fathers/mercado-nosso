package com.mercadonosso.orders_service.adapters.in;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.mercadonosso.orders_service.adapters.in.dto.CreatingOrderRequest;
import com.mercadonosso.orders_service.adapters.in.dto.DashboardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.mercadonosso.orders_service.adapters.in.dto.OrderResponse;
import com.mercadonosso.orders_service.adapters.in.dto.UpdateOrderStatusRequest;
import com.mercadonosso.orders_service.core.domain.Order;
import com.mercadonosso.orders_service.core.ports.in.OrdersServicePort;

@RestController
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final RestTemplate restTemplate;
    private final OrdersServicePort ordersServicePort;

    public OrderController(OrdersServicePort ordersServicePort, RestTemplate restTemplate) {
        this.ordersServicePort = ordersServicePort;
        this.restTemplate = restTemplate;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody CreatingOrderRequest request) {
        logger.info("Creating order for buyer: {} and seller: {}", request.buyerId(), request.sellerId());

        Order createdOrder = ordersServicePort.createOrderWithUserUpdates(request);

        return toResponse(createdOrder);
    }

    private OrderResponse toResponse(Order order) {
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
        logger.debug("Fetching orders for buyer ID: {}", id);

        List<Order> orders = ordersServicePort.findByBuyerId(id);
        logger.debug("Found {} orders for buyer {}", orders.size(), id);

        return orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/seller/{sellerId}")
    public List<OrderResponse> getBySellerId(@PathVariable UUID sellerId) {
        logger.debug("Fetching orders for seller ID: {}", sellerId);

        List<Order> orders = ordersServicePort.findBySellerId(sellerId);
        logger.debug("Found {} orders for seller {}", orders.size(), sellerId);

        return orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable UUID id) {
        logger.info("Inactivating order with ID: {}", id);
        ordersServicePort.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    public OrderResponse updateOrder(@PathVariable UUID id, @RequestBody UpdateOrderStatusRequest request) {
        Order orderToUpdate = ordersServicePort.updateOrder(id, request.getStatus());
        return toResponse(orderToUpdate);
    }

    @GetMapping("/seller/{sellerId}/dashboard")
    public DashboardResponse getDashboard(@PathVariable UUID sellerId) {
        List<Order> orders = ordersServicePort.findBySellerId(sellerId);

        List<DashboardResponse.OrderWithListing> ordersWithListings = new ArrayList<>();

        for (Order order : orders) {
            List<DashboardResponse.ListingInfo> listingInfos = new ArrayList<>();

            for (String listingId : order.getListingId()) {
                try {
                    // TODO: replace this with an environment variable or configuration
                    String url = "http://listings-service:8084/" + listingId;

                    @SuppressWarnings("unchecked")
                    Map<String, Object> listingResponse = restTemplate.getForObject(url, Map.class);
                    logger.debug("URL called: {}", url);
                    logger.debug("Response: {}", listingResponse);

                    if (listingResponse != null) {
                        String title = (String) listingResponse.get("title");
                        BigDecimal price = new BigDecimal(listingResponse.get("price").toString());
                        String category = (String) listingResponse.get("category");
                        Integer rating = (Integer) listingResponse.get("rating");
                        Integer salesCount = (Integer) listingResponse.get("salesCount");

                        listingInfos.add(new DashboardResponse.ListingInfo(
                                listingId,
                                title,
                                price,
                                category,
                                rating,
                                salesCount));
                    }
                } catch (Exception e) {
                    logger.error("Error fetching listing {}: {}", listingId, e.getMessage());
                }
            }

            ordersWithListings.add(new DashboardResponse.OrderWithListing(
                    order.getOrderId(),
                    order.getBuyerId(),
                    order.getSellerId(),
                    order.getStatus(),
                    order.getDate(),
                    listingInfos));

        }

        BigDecimal totalSales = BigDecimal.ZERO;
        double totalRating = 0.0;
        int ratingCount = 0;

        for (DashboardResponse.OrderWithListing orderWithListing : ordersWithListings) {
            for (DashboardResponse.ListingInfo listing : orderWithListing.listings()) {
                totalSales = totalSales.add(listing.price());
            }
        }

        double averageRating = ratingCount > 0 ? totalRating / ratingCount : 0.0;

        return new DashboardResponse(
                totalSales,
                averageRating,
                ordersWithListings,
                new ArrayList<>());
    }
}
