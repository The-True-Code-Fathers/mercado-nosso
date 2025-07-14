package com.mercadonosso.listings_service.core.ports.in;

import java.util.List;

import org.bson.types.ObjectId;

import com.mercadonosso.listings_service.core.domain.ListingsEntity;

public interface ListingsServicePort {
    ListingsEntity create(ListingsEntity listingsEntity);

    ListingsEntity update(ObjectId id, ListingsEntity listingsEntity);

    void delete(ListingsEntity listingsEntity);

    ListingsEntity findById(ObjectId id);

    List<ListingsEntity> listAll();
}
