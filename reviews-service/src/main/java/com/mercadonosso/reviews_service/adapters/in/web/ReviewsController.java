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
        System.out.println("CONTROLLER - DTO Recebido: " + request.toString());

        ReviewsEntity reviewToCreate = toDomain(request);

        System.out.println("CONTROLLER - Entidade Mapeada para Enviar ao Serviço: " + reviewToCreate.toString());

        ReviewsEntity createdReview = reviewsServicePort.create(reviewToCreate);
        return toResponse(createdReview);
    }

    private ReviewsResponse toResponse(ReviewsEntity entity) {
        return new ReviewsResponse(
                entity.getId(),
                entity.getListingId(),
                entity.getBuyerId(),
                entity.getRating(),
                entity.getMessage(),
                entity.getImagesUrls(),
                entity.getCreatedAt(),
                entity.getSellerId());
    }

    private ReviewsEntity toDomain(CreateReviewsRequest dto) {
        ReviewsEntity domain = new ReviewsEntity();
        domain.setListingId(dto.listingId());
        domain.setBuyerId(dto.buyerId());
        domain.setRating(dto.rating());
        domain.setMessage(dto.message());
        domain.setImagesUrls(dto.imagesUrls());
        return domain;
    }

    @GetMapping("/{id}")
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

    @GetMapping("/listing/{listingId}")
    public List<ReviewsResponse> findByListingId(@PathVariable String listingId) {
        List<ReviewsEntity> reviewsEntities = reviewsServicePort.findByListingId(listingId);
        return reviewsEntities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        reviewsServicePort.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ReviewsResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateReviewsRequest request) {
        ReviewsEntity reviewWithNewData = new ReviewsEntity();
        reviewWithNewData.setRating(request.rating());
        reviewWithNewData.setMessage(request.message());

        ReviewsEntity updatedReview = reviewsServicePort.update(id, reviewWithNewData);
        return toResponse(updatedReview);
    }
}