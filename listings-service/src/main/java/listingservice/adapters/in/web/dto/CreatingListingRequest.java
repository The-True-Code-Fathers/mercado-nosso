package listingservice.adapters.in.web.dto;

import listingservice.core.domain.enums.ProductCondition;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatingListingRequest(UUID productId,
                                     UUID sellerId,
                                     String title,
                                     String description,
                                     BigDecimal price,
                                     Integer stock,
                                     ProductCondition productCondition) {
}
