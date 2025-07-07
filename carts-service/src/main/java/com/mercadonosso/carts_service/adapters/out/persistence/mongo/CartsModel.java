package com.mercadonosso.carts_service.adapters.out.persistence.mongo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mercadonosso.carts_service.core.domain.CartsEntity.CartItemEntity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Data
@Document(collection = "carts")
public class CartsModel {
    @Id
    private UUID id;

    @Indexed(unique = true)
    @Field("user_id")
    private UUID userId;

    @Field("listings_id")
    private UUID listingId;

    private List<CartItemEntity> items;

    @Field(name = "sub_total", targetType = FieldType.DECIMAL128)
    private BigDecimal subTotal;

    @Field(name = "grand_total", targetType = FieldType.DECIMAL128)
    private BigDecimal grandTotal;

    @Field(name = "shipping_price_total", targetType = FieldType.DECIMAL128)
    private BigDecimal shippingPriceTotal;

    @Field("updated_at")
    private LocalDateTime updateAt;
}
