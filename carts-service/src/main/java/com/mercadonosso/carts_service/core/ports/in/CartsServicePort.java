package com.mercadonosso.carts_service.core.ports.in;

import java.util.List;
import java.util.UUID;

import com.mercadonosso.carts_service.core.domain.CartsEntity;

public interface CartsServicePort {
    CartsEntity findById(UUID userId);

    CartsEntity create(UUID userId, UUID listingId, int quantity);

    CartsEntity remove(UUID userId, UUID listingId);

    CartsEntity update(UUID userId, UUID listingId, int quantity);

    void requestClear(UUID userId);

    void processClear(UUID userId);

    void requestRemove(UUID userId, List<UUID> listingsIds); 

    void processRemove(UUID userId, String listingsIdsJson);
}
