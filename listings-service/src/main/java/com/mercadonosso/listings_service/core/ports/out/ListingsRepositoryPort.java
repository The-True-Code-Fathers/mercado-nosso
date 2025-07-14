package com.mercadonosso.listings_service.core.ports.out;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import com.mercadonosso.listings_service.core.domain.ListingsEntity;

@Repository
public interface ListingsRepositoryPort {
    ListingsEntity save(ListingsEntity listingsEntity);

    void delete(ListingsEntity listingsEntity);

    List<ListingsEntity> listAll();

    Optional<ListingsEntity> findById(ObjectId id);
}
