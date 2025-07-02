package com.mercadonosso.carts_service.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
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
    @Id
    private UUID id;
    @Field("user_id")
    private UUID userId;
    @Field("listings_id")
    private List<CartItemEntity> items;
    @Field(name = "shipping_price_total", targetType = FieldType.DECIMAL128)
    private BigDecimal shippingPriceTotal;
    @Field(name = "sub_total", targetType = FieldType.DECIMAL128)
    private BigDecimal subTotal;
    @Field(name = "grand_total", targetType = FieldType.DECIMAL128)
    private BigDecimal grandTotal;
    @Field("updated_at")
    private LocalDateTime updateAt;

    public CartsEntity(UUID userId) {
        this.userId = userId;
        this.subTotal = BigDecimal.ZERO;
        this.shippingPriceTotal = BigDecimal.ZERO;
        this.grandTotal = BigDecimal.ZERO;
        this.updateAt = LocalDateTime.now();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CartItemEntity {
        private UUID listingId;
        private int quantity;
        @Field(targetType = FieldType.DECIMAL128)
        private BigDecimal price;
        @Field(targetType = FieldType.DECIMAL128)
        private BigDecimal shippingPrice;    
    }

}
