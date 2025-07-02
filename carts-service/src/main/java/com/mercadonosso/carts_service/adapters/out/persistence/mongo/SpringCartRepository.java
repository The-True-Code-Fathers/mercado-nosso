package com.mercadonosso.carts_service.adapters.out.persistence.mongo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringCartRepository extends MongoRepository<CartsModel, UUID>{
    Optional<CartsModel> findByUserId(UUID userId);
}
