package com.mercadonosso.orders_service.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethod {
    private String id;

    @NotBlank(message = "Payment type is required")
    private PaymentMethod type; // CREDIT_CARD, DEBIT_CARD, PIX, etc.

    private String cardNumber;

    private String cardholderName;

    private String expiryDate;

    @Min(value = 1, message = "Installments must be at least 1")
    private Integer installments = 1;
}
