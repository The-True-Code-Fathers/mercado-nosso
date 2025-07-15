package com.mercadonosso.carts_service.core.ports.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.bson.types.ObjectId;
import com.mercadonosso.carts_service.core.domain.CartsEntity;

public interface CartsServicePort {
    CartsEntity findById(UUID userId);

    CartsEntity create(UUID userId, ObjectId listingId, int quantity, BigDecimal price);

    CartsEntity remove(UUID userId, ObjectId listingId);

    CartsEntity update(UUID userId, ObjectId listingId, int quantity);

    void requestClear(UUID userId);

    void processClear(UUID userId);

    void requestRemove(UUID userId, List<ObjectId> listingsIds);

    void processRemove(UUID userId, String listingsIdsJson);
}
