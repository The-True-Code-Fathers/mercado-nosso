package com.mercadonosso.reviews_service.core.usecases;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mercadonosso.reviews_service.core.domain.ReviewsEntity;
import com.mercadonosso.reviews_service.core.domain.exception.ReviewsNotFoundException;
import com.mercadonosso.reviews_service.core.ports.in.ReviewsServicePort;
import com.mercadonosso.reviews_service.core.ports.out.ListingServiceClient;
import com.mercadonosso.reviews_service.core.ports.out.ReviewsRepositoryPort;

import jakarta.validation.Validator;

@Service
public class ReviewsServiceImpl implements ReviewsServicePort {
    private final ReviewsRepositoryPort reviewsRepositoryPort;
    @SuppressWarnings("unused")
    private final Validator validator;
    private final ListingServiceClient listingsServiceClient;

    public ReviewsServiceImpl(ReviewsRepositoryPort reviewsRepositoryPort,
            Validator validator,
            ListingServiceClient listingsServiceClient) {
        this.reviewsRepositoryPort = reviewsRepositoryPort;
        this.validator = validator;
        this.listingsServiceClient = listingsServiceClient;
    }

    @Override
    @Transactional
    public ReviewsEntity create(ReviewsEntity reviewData) {
        var listing = listingsServiceClient.findListingById(reviewData.getListingId());

        if (listing == null) {
            throw new IllegalArgumentException("Listing not found with ID: " + reviewData.getListingId());
        }

        ReviewsEntity newReview = ReviewsEntity.builder()
                .listingId(reviewData.getListingId())
                .buyerId(reviewData.getBuyerId())
                .rating(reviewData.getRating())
                .message(reviewData.getMessage())
                .imagesUrls(reviewData.getImagesUrls())
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .active(true)
                .sellerId(UUID.fromString(listing.sellerId())) // Convert String to UUID
                .build();

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
        return reviewsRepositoryPort.findById(id)
                .orElseThrow(() -> new ReviewsNotFoundException("Review com o id " + id + " não encontrado."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewsEntity> findByListingId(String listingId) {
        return reviewsRepositoryPort.findByListingId(listingId);
    }
}