package com.mercadonosso.listings_service.adapters.out.persistence.mongo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringListingsRepository extends MongoRepository<ListingsModel, ObjectId> {

//    Optional<ListingsModel> findById(ObjectId id);
}
