package com.mercadonosso.reviews_service.adapters.out.mongo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

// Precisa criar um bean ainda
//@Data
//@Document(collection = "reviews");
public class ReviewModel {
    @Id
    private UUID id;

    @Field("listing_id")
    private UUID listingId;

    @Field("buyer_id")
    private UUID buyerId;

    @Field("seller_id")
    private UUID sellerId;

    private Integer rating;

    private String message;

    @Field("images_urls")
    private List<String> imagesUrls;

    @Field("created_at")
    private LocalDateTime createdAt;
}
