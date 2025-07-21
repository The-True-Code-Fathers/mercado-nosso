package com.mercadonosso.orders_service.adapters.out.mongo.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.mercadonosso.orders_service.core.domain.enums.PaymentType;

/**
 * Value Object para representar método de pagamento no MongoDB
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodModel {

    private String id;
    private PaymentType type; // Enum PaymentType
    private String cardNumber; // Sempre mascarado
    private String cardholderName;
    private String expiryDate;
    private String cvv; // Código de segurança do cartão
    private Integer installments;
    private String pixKey; // Para pagamentos PIX
}
