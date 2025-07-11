package com.mercadonosso.listings_service.core.ports.out;

import com.mercadonosso.listings_service.core.domain.ListingsEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public interface ListingsRepositoryPort {
    ListingsEntity save(ListingsEntity listingsEntity);
    void delete(ListingsEntity listingsEntity);
    List<ListingsEntity> listAll();
    Optional<ListingsEntity> findById(ObjectId id);
}
