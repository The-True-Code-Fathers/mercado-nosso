package com.mercadonosso.carts_service.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartsResponse {
    private UUID id;
    private UUID userId;
    private List<CartsItemResponse> items;
    private BigDecimal subTotal;
    private BigDecimal shippingPriceTotal;
    private BigDecimal grandTotal;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartsItemResponse {
        private String listingId;
        private int quantity;
        private BigDecimal price;
        private BigDecimal shippingPrice;
    }
}
