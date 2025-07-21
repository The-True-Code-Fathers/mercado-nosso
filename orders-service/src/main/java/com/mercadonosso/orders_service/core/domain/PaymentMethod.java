package com.mercadonosso.orders_service.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import com.mercadonosso.orders_service.core.domain.enums.PaymentType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethod {
    private String id;

    @NotNull(message = "Payment type is required")
    private PaymentType type;

    private String cardNumber; // Sempre mascarado para segurança

    private String cardholderName;

    private String expiryDate;

    @Min(value = 1, message = "Installments must be at least 1")
    private Integer installments;

    // Para PIX ou outros métodos
    private String pixKey;

    // Método de conveniência para verificar se é cartão
    public boolean isCreditCard() {
        return PaymentType.CREDIT_CARD.equals(type);
    }

    public boolean isDebitCard() {
        return PaymentType.DEBIT_CARD.equals(type);
    }

    public boolean isPix() {
        return PaymentType.PIX.equals(type);
    }
}
