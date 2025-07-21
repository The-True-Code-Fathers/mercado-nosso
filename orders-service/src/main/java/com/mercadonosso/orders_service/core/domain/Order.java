package com.mercadonosso.orders_service.core.domain;

import lombok.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import com.mercadonosso.orders_service.core.domain.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    private UUID orderId;

    @NotNull(message = "Must have buyer ID.")
    private UUID buyerId;

    @Field("created_at")
    private LocalDateTime date;

    @NotNull(message = "Must have a status")
    @Field("order_status")
    private OrderStatus status;

    @NotNull(message = "Must have at least one item")
    @Field("order_items")
    private List<OrderItem> orderItems;

    @NotNull(message = "Seller id must not be null")
    private UUID sellerId;
    @Valid
    @NotNull(message = "Shipping address is required")
    private ShippingAddress shippingAddress;

    @Valid
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Valid
    @NotNull(message = "Order summary is required")
    private OrderSummary orderSummary;

    /**
     * Retorna uma lista com todos os listing IDs do pedido
     * @return Lista de listing IDs
     */
    public List<String> getListingIds() {
        if (orderItems == null) {
            return List.of();
        }
        return orderItems.stream()
                .map(OrderItem::getListingId)
                .toList();
    }
    
    /**
     * Retorna a quantidade total de items no pedido
     * @return Quantidade total de items
     */
    public Integer getTotalQuantity() {
        if (orderItems == null) {
            return 0;
        }
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }
    
    /**
     * Busca um item específico por listing ID
     * @param listingId ID do listing
     * @return OrderItem encontrado ou null
     */
    public OrderItem getItemByListingId(String listingId) {
        if (orderItems == null || listingId == null) {
            return null;
        }
        return orderItems.stream()
                .filter(item -> listingId.equals(item.getListingId()))
                .findFirst()
                .orElse(null);
    }
}
