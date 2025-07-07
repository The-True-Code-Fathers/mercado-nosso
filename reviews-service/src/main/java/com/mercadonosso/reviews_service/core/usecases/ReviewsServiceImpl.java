package com.mercadonosso.reviews_service.core.usecases;

import com.mercadonosso.reviews_service.core.domain.ReviewsEntity;
import com.mercadonosso.reviews_service.core.domain.exception.ReviewsNotFoundException;
import com.mercadonosso.reviews_service.core.ports.in.ReviewsServicePort;
import com.mercadonosso.reviews_service.core.ports.out.ReviewsRepositoryPort;
import jakarta.validation.Validator;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public ReviewsEntity create(ReviewsEntity reviewData) {
        System.out.println("LOGGER 1 COLADO COM CUSPE - Entidade Recebida do Controller: " + reviewData.toString());

        ReviewsEntity newReview = ReviewsEntity.builder()
                .listingId(reviewData.getListingId())
                .buyerId(reviewData.getBuyerId())
                .rating(reviewData.getRating())
                .message(reviewData.getMessage())
                .imagesUrls(reviewData.getImagesUrls())
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .active(true)
                .sellerId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .build();

        System.out.println("LOGGER 2 COLADO COM CHICLETE - Entidade Final para Salvar: " + newReview.toString());

        return reviewsRepositoryPort.save(newReview);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ReviewsEntity reviewToDelete = this.findById(id);
        reviewsRepositoryPort.delete(reviewToDelete);
    }

    @Override
    @Transactional
    public ReviewsEntity update(UUID id, ReviewsEntity newReviewData) {
        ReviewsEntity existingReview = this.findById(id);
        existingReview.setRating(newReviewData.getRating());
        existingReview.setMessage(newReviewData.getMessage());
        return reviewsRepositoryPort.save(existingReview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewsEntity> listAll() {
        return reviewsRepositoryPort.listAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewsEntity findById(UUID id) {
        return reviewsRepositoryPort.findById(id).orElseThrow(() ->
                new ReviewsNotFoundException("Review com o id " + id + " não encontrado."));
    }
}