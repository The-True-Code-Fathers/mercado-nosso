package com.mercadonosso.reviews_service.core.ports.out;
import com.mercadonosso.reviews_service.core.domain.ReviewsEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewsRepositoryPort {
    ReviewsEntity save(ReviewsEntity reviewsEntity);
    void delete(ReviewsEntity reviewsEntity);
    List<ReviewsEntity> listAll();
    Optional<ReviewsEntity> findById(UUID id);
}
