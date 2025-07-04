package com.mercadonosso.reviews_service.core.usecases;

import com.mercadonosso.reviews_service.core.domain.ReviewsEntity;
import com.mercadonosso.reviews_service.core.domain.exception.ReviewsNotFoundException;
import com.mercadonosso.reviews_service.core.ports.in.ReviewsServicePort;
import com.mercadonosso.reviews_service.core.ports.out.ReviewsRepositoryPort;
import jakarta.validation.Validator;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ReviewsServiceImpl implements ReviewsServicePort {
    private final ReviewsRepositoryPort reviewsRepositoryPort;
    private final Validator validator;

    public ReviewsServiceImpl(ReviewsRepositoryPort reviewsRepositoryPort, Validator validator) {
        this.reviewsRepositoryPort = reviewsRepositoryPort;
        this.validator = validator;
    }

    @Override
    public ReviewsEntity create(ReviewsEntity reviewsEntity) {
        reviewsEntity.setCreatedAt(LocalDateTime.now());
        reviewsEntity.setActive(true);
        reviewsEntity.setId(UUID.randomUUID());

        return reviewsRepositoryPort.save(reviewsEntity);
    }

    @Override
    public void delete(ReviewsEntity reviewsEntity) {
        reviewsRepositoryPort.delete(reviewsEntity);
    }

    @Override
    public ReviewsEntity update(UUID id, ReviewsEntity newReviewData) {
        ReviewsEntity existingReview = this.findById(id);

        existingReview.setRating(newReviewData.getRating());
        existingReview.setMessage(newReviewData.getMessage());

        return reviewsRepositoryPort.save(existingReview);
    }

    @Override
    public List<ReviewsEntity> listAll() {
        return reviewsRepositoryPort.listAll();
    }

    @Override
    public ReviewsEntity findById(UUID id) {
        return reviewsRepositoryPort.findById(id).orElseThrow(() ->
                new ReviewsNotFoundException("Review com o id " + id + " não encontrado."));
    }
}
