package com.mercadonosso.carts_service.core.ports.out;

import java.math.BigDecimal;

import org.bson.types.ObjectId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListingDetails {
    private ObjectId listingId;
    private BigDecimal price;
    private int stock;
}
