package com.mercadonosso.listings_service.adapters.out.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringListingsRepository extends MongoRepository<ListingsModel, String> {

}
