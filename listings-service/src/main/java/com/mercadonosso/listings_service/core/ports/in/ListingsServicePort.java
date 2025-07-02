package com.mercadonosso.listings_service.core.ports.in;

import com.mercadonosso.listings_service.core.domain.ListingsEntity;

import java.util.List;
import java.util.UUID;

public interface ListingsServicePort {
    ListingsEntity create(ListingsEntity listingsEntity);
    ListingsEntity searchById(UUID id);
    void delete(ListingsEntity listingsEntity);
    List<ListingsEntity> listAll();
}
