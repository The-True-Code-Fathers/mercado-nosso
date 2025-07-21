package com.mercadonosso.orders_service.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingAddress {

    @NotBlank(message = "Street is required")
    private String street;

    @NotBlank(message = "Number is required")
    private String number;

    private String complement;

    @NotBlank(message = "Neighborhood is required")
    private String neighborhood;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 2, message = "State must be 2 characters")
    private String state;

    @NotBlank(message = "Zip code is required")
    private String zipCode;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String id;

    private boolean isDefault;

    @Override
    public String toString() {
        return String.format("%s, %s%s - %s - %s/%s - %s",
                street, number,
                (complement != null && !complement.isEmpty()) ? ", " + complement : "",
                neighborhood, city, state, zipCode);
    }
}
