package com.mercadonosso.products_service.adapters.out.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringMongoProductsRepository extends MongoRepository<ProductsModel, UUID> {

}
