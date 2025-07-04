package com.mercadonosso.listings_service.adapters.out.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringListingsRepository extends MongoRepository<ListingsModel, UUID> {

}
