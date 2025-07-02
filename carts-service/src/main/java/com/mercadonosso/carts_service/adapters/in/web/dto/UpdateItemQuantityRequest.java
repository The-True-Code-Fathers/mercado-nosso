package com.mercadonosso.carts_service.adapters.in.web.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemQuantityRequest {
    private UUID userId;
    private UUID listingId;
    private int quantity;
}