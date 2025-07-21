package com.mercadonosso.listings_service.adapters.out.persistence.mongo;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringListingsRepository extends MongoRepository<ListingsModel, ObjectId> {

    /**
     * Finds all active listings.
     */
    @Query("{ 'active': true }")
    List<ListingsModel> findAllActiveListings();

    @Query("{ 'active': true, sku: ?0}")
    Optional<ListingsModel> findBySku(String sku);

}
