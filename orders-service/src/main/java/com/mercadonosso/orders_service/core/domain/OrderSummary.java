package com.mercadonosso.orders_service.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSummary {

    @NotNull(message = "Subtotal is required")
    @PositiveOrZero(message = "Subtotal must be positive or zero")
    private BigDecimal subtotal;

    @PositiveOrZero(message = "Shipping total must be positive or zero")
    private BigDecimal shippingTotal;

    @PositiveOrZero(message = "Discount total must be positive or zero")
    private BigDecimal discountTotal;

    @NotNull(message = "Total is required")
    @PositiveOrZero(message = "Total must be positive or zero")
    private BigDecimal total;

    @PositiveOrZero(message = "Items count must be positive or zero")
    private Integer itemsCount;

    // Construtor de conveniência
    public OrderSummary(BigDecimal subtotal, BigDecimal shippingTotal, BigDecimal discountTotal, Integer itemsCount) {
        this.subtotal = subtotal;
        this.shippingTotal = shippingTotal != null ? shippingTotal : BigDecimal.ZERO;
        this.discountTotal = discountTotal != null ? discountTotal : BigDecimal.ZERO;
        this.itemsCount = itemsCount;

        // Calcula o total automaticamente
        this.total = subtotal
                .add(this.shippingTotal)
                .subtract(this.discountTotal);
    }

    // Método para recalcular o total
    public void recalculateTotal() {
        this.total = (subtotal != null ? subtotal : BigDecimal.ZERO)
                .add(shippingTotal != null ? shippingTotal : BigDecimal.ZERO)
                .subtract(discountTotal != null ? discountTotal : BigDecimal.ZERO);
    }
}
