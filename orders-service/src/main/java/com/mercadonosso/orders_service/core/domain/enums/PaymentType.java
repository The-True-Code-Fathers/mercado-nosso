package com.mercadonosso.orders_service.core.domain.enums;

public enum PaymentType {
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    PIX("PIX"),
    BANK_TRANSFER("Bank Transfer"),
    BOLETO("Boleto");

    private final String displayName;

    PaymentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
