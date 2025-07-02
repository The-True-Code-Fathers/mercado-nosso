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

@Data
@Document(collection = "carts")
public class CartsModel {
    @Id
    private UUID id;
    @Indexed(unique = true)
    private UUID userId;
    private UUID listingId;
    private List<CartItemEntity> items;
    private BigDecimal subTotal;
    private BigDecimal grandTotal;
    private BigDecimal shippingPriceTotal;
    private LocalDateTime updateAt;

}
