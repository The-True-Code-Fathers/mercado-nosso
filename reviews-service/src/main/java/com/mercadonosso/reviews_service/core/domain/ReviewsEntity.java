package com.mercadonosso.reviews_service.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewsEntity {
    private UUID id;
    private LocalDateTime createdAt;
    private boolean active;
    private UUID sellerId;
    private UUID listingId;
    private UUID buyerId;
    private Integer rating;
    private String message;
    private List<String> imagesUrls;
}