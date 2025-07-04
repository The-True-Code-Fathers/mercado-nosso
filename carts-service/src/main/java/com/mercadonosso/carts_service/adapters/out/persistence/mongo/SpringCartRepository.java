package com.mercadonosso.carts_service.adapters.out.persistence.mongo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringCartRepository extends MongoRepository<CartsModel, UUID>{
    Optional<CartsModel> findByUserId(UUID userId);
}
