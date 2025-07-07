package com.mercadonosso.reviews_service.adapters.in.web;

import com.mercadonosso.reviews_service.adapters.in.web.dto.CreateReviewsRequest;
import com.mercadonosso.reviews_service.adapters.in.web.dto.ReviewsResponse;
import com.mercadonosso.reviews_service.adapters.in.web.dto.UpdateReviewsRequest;
import com.mercadonosso.reviews_service.core.domain.ReviewsEntity;
import com.mercadonosso.reviews_service.core.ports.in.ReviewsServicePort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @GetMapping("{/id}")
    public ReviewsResponse findById(@PathVariable UUID id) {
        ReviewsEntity reviewsEntity = reviewsServicePort.findById(id);
        return toResponse(reviewsEntity);
    }

    @GetMapping
    public List<ReviewsResponse> listAll() {
        List<ReviewsEntity> reviewsEntities = reviewsServicePort.listAll();
        return reviewsEntities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        reviewsServicePort.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("{/id}")
    public ReviewsResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateReviewsRequest request) {
        ReviewsEntity reviewWithNewData = new ReviewsEntity();
        reviewWithNewData.setRating(request.rating());
        reviewWithNewData.setMessage(request.message());

        ReviewsEntity updatedReview = reviewsServicePort.update(id, reviewWithNewData);
        return toResponse(updatedReview);
    }
}