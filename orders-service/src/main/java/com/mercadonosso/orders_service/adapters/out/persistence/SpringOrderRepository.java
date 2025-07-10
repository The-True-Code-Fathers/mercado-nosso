package com.mercadonosso.orders_service.adapters.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.mercadonosso.orders_service.adapters.out.mongo.OrderModel;

import java.util.UUID;

@Repository
public interface SpringOrderRepository extends MongoRepository<OrderModel, UUID> {

}
