package com.mercadonosso.orders_service.adapters.out.mongo.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value Object para representar endereço de entrega no MongoDB
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingAddressModel {

    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;
    private String fullName;
    private String id;
    private boolean isDefault;
}
