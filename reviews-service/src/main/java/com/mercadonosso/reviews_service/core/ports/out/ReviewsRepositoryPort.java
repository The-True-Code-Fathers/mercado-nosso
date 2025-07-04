package com.mercadonosso.reviews_service.core.ports.out;
import com.mercadonosso.reviews_service.core.domain.ReviewsEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewsRepositoryPort {
    ReviewsEntity save(ReviewsEntity reviewsEntity);
    void delete(ReviewsEntity reviewsEntity);
    List<ReviewsEntity> listAll();
    Optional<ReviewsEntity> findById(UUID id);
}
