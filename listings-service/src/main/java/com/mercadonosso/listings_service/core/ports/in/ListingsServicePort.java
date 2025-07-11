package com.mercadonosso.listings_service.core.ports.in;

import com.mercadonosso.listings_service.core.domain.ListingsEntity;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.UUID;

public interface ListingsServicePort {
    ListingsEntity create(ListingsEntity listingsEntity);
    ListingsEntity update(ObjectId id, ListingsEntity listingsEntity);
    void delete(ListingsEntity listingsEntity);
    ListingsEntity findById(ObjectId id);
    List<ListingsEntity> listAll();
}
