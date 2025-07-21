package com.mercadonosso.orders_service.adapters.out.mongo.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value Object para representar método de pagamento no MongoDB
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodModel {
    
    private String id;
    private String type; // CREDIT_CARD, DEBIT_CARD, PIX, etc.
    private String cardNumber; // Sempre mascarado
    private String cardholderName;
    private String expiryDate;
    private Integer installments;
    private String pixKey; // Para pagamentos PIX
}
