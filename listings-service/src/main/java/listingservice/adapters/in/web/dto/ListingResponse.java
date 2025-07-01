package listingservice.adapters.in.web.dto;

import listingservice.core.domain.enums.ProductCondition;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ListingResponse(
    UUID listingId,
    String title,
    String description,
    BigDecimal price,
    Integer stock,
    boolean active,
    ProductCondition productCondition,
    LocalDateTime creationTime
) { }
