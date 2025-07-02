package com.mercadonosso.listings_service.core.ports.out;

import com.mercadonosso.listings_service.core.domain.ListingsEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListingsRepositoryPort {
    ListingsEntity save(ListingsEntity listingsEntity);
    void delete(ListingsEntity listingsEntity);
    List<ListingsEntity> listAll();
    Optional<ListingsEntity> searchById(UUID id);
}
