package com.mercadonosso.carts_service.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartsEntity {
    private UUID id;
    private UUID userId;
    private List<CartItemEntity> items = new ArrayList<>();
    private BigDecimal shippingPriceTotal;
    private BigDecimal subTotal;
    private BigDecimal grandTotal;
    private LocalDateTime updateAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CartItemEntity {
        private ObjectId listingId;
        private int quantity;
        @Field(targetType = FieldType.DECIMAL128)
        private BigDecimal price;
        @Field(targetType = FieldType.DECIMAL128)
        private BigDecimal shippingPrice;
    }

    public CartsEntity(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.subTotal = BigDecimal.ZERO;
        this.shippingPriceTotal = BigDecimal.ZERO;
        this.grandTotal = BigDecimal.ZERO;
        this.updateAt = LocalDateTime.now();
    }
}
