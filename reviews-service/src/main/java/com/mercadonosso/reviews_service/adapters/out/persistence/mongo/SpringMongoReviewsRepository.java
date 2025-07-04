package com.mercadonosso.reviews_service.adapters.out.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringMongoReviewsRepository extends MongoRepository<ReviewsModel, String> {

}
