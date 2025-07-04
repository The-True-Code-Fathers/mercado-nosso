package com.mercadonosso.carts_service.adapters.in.web.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddItemRequest {
    @NotNull(message = "Listing ID can not be null")
    private UUID listingId;
    @Min(value = 1, message = "Quantity needs to be higher than 0")
    private int quantity;
}
