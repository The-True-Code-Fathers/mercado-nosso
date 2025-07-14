package com.mercadonosso.carts_service.adapters.in.web.dto;

import java.util.List;

import org.bson.types.ObjectId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveListingRequest {
    List<ObjectId> listingsIds;
}
