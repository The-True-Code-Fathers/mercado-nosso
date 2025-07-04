package com.mercadonosso.listings_service.adapters.out.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface SpringListingsRepository extends MongoRepository<ListingsModel, UUID> {

}
