package core.domain;

import core.domain.enums.OrderStatus;
import lombok.*;
import org.jetbrains.annotations.NotNull;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Orders {

    private UUID orderId;
    @NotNull
    private UUID buyerId;
    private LocalDateTime date;
    private OrderStatus status;
    @NotNull
    private List<UUID> listingId;

}
