package listingservice.adapters.out.persistence.mongo;

import listingservice.core.domain.enums.ProductCondition;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Document(collection = "listings")
public class ListingModel {
    @Id
    private String id;

    private UUID productId;
    private UUID sellerId;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private boolean active;
    private LocalDateTime creationDate;
    private ProductCondition productCondition;
}
