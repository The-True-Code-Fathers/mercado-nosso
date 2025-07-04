package com.mercadonosso.orders_service.orderservice.adapters.out.persistence;

import com.mercadonosso.orders_service.orderservice.adapters.out.mongo.OrderModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringOrderRepository extends MongoRepository<OrderModel, UUID> {

}
