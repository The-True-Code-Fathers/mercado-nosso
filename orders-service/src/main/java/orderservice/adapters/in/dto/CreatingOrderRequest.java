package orderservice.adapters.in.dto;

import orderservice.core.domain.enums.OrderStatus;


import java.util.List;
import java.util.UUID;

public record CreatingOrderRequest(UUID orderId,
                                   UUID buyerId,
                                   OrderStatus status,
                                   List<UUID> listing
                                   ){
}
