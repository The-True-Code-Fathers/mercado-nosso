package com.mercadonosso.orders_service.adapters.out.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mercadonosso.orders_service.core.domain.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Document(collection = "orders")
public class OrderModel {

    @Id
    private UUID orderId;

    private UUID buyerId;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private List<UUID> productIds;

}
