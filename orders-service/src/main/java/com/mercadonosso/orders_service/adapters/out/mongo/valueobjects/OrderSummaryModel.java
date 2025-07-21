package com.mercadonosso.orders_service.adapters.out.mongo.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Value Object para representar resumo do pedido no MongoDB
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSummaryModel {

    private BigDecimal subtotal;
    private BigDecimal shippingTotal;
    private BigDecimal discountTotal;
    private BigDecimal total;
    private Integer itemsCount;
}
