package orderservice.core.domain;

import orderservice.core.domain.enums.OrderStatus;
import lombok.*;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orders {

    private UUID orderId;

    @NotNull(message = "Must have buyer ID.")
    private UUID buyerId;

    private LocalDateTime date;

    private OrderStatus status;

    @NotNull(message = "Must have an item")
    private List<UUID> listingId;

}
