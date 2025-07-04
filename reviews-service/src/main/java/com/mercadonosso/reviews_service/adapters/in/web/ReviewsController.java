package com.mercadonosso.reviews_service.adapters.in.web;

import com.mercadonosso.reviews_service.adapters.in.web.dto.CreateReviewsRequest;
import com.mercadonosso.reviews_service.adapters.in.web.dto.ReviewsResponse;
import com.mercadonosso.reviews_service.core.domain.ReviewsEntity;
import com.mercadonosso.reviews_service.core.ports.in.ReviewsServicePort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ReviewsController {
    private final ReviewsServicePort reviewsServicePort;

    public ReviewsController(ReviewsServicePort reviewsServicePort) {
        this.reviewsServicePort = reviewsServicePort;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewsResponse createReviews(@Valid @RequestBody CreateReviewsRequest request) {
        ReviewsEntity reviewsEntity = new ReviewsEntity();
        reviewsEntity.setListingId(request.listingId());
        reviewsEntity.setBuyerId(request.buyerId());
        reviewsEntity.setRating(request.rating());
        reviewsEntity.setMessage(request.message());
        reviewsEntity.setActive(true);
        reviewsEntity.setImagesUrls(request.imagesUrls());
        reviewsEntity.setCreatedAt(request.createdAt());
        reviewsEntity.setId(request.id());
        ReviewsEntity createdReview = reviewsServicePort.create(reviewsEntity);

        return toResponse(createdReview);
    }

    private ReviewsResponse toResponse(ReviewsEntity reviewsEntity) {
        return new ReviewsResponse(
                reviewsEntity.getId(),
                reviewsEntity.getListingId(),
                reviewsEntity.getBuyerId(),
                reviewsEntity.getRating(),
                reviewsEntity.getMessage(),
                reviewsEntity.getImagesUrls(),
                reviewsEntity.getCreatedAt()
        );
    }
}