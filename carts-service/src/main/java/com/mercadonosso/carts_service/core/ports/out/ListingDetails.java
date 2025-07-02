package com.mercadonosso.carts_service.core.ports.out;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListingDetails {
    private UUID listingId;
    private BigDecimal price;
    private int stock;
}
