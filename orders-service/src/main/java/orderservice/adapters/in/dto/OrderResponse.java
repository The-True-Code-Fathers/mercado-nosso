package orderservice.adapters.in.dto;

import orderservice.core.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse (
        UUID orderId,
        UUID buyerId,
        List<UUID> listingID,
        OrderStatus status,
        LocalDateTime creationTime
){
}
