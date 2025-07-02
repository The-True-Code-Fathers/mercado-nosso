package com.mercadonosso.listings_service.core.ports.in;

import com.mercadonosso.listings_service.core.domain.ListingsEntity;

import java.util.List;
import java.util.UUID;

public interface ListingsServicePort {
    ListingsEntity create(ListingsEntity listingsEntity);
    ListingsEntity update(UUID id, ListingsEntity listingsEntity);
    void delete(ListingsEntity listingsEntity);
    ListingsEntity searchById(UUID id);
    List<ListingsEntity> listAll();
}
