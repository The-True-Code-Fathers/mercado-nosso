package com.mercadonosso.orders_service.orderservice.adapters.out.persistence;

import com.mercadonosso.orders_service.orderservice.adapters.out.mongo.OrderModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringOrderRepository extends MongoRepository<OrderModel, String> {

}
