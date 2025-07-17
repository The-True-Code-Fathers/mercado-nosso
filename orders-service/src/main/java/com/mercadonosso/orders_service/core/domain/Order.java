package com.mercadonosso.orders_service.core.domain;

import lombok.*;

import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Must have an item")
    @Field("list_of_listings")
    private List<String> listingId;

}
