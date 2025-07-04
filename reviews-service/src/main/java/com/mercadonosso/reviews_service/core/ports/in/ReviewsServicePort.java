package com.mercadonosso.reviews_service.core.ports.in;
import com.mercadonosso.reviews_service.core.domain.ReviewsEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewsServicePort {
    ReviewsEntity create(ReviewsEntity reviewsEntity);
    void delete(ReviewsEntity reviewsEntity);
    ReviewsEntity update(UUID id, ReviewsEntity reviewsEntity);
    List<ReviewsEntity> listAll();
    ReviewsEntity findById(UUID id);
}