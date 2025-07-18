package com.mercadonosso.orders_service.adapters.out.rest.users;

import java.util.List;
import java.util.UUID;

public record UpdateUserRequest(
        String fullName,
        String profilePictureUrl,
        String email,
        String telephoneNumber,
        String cnpj,
        String socialReason,
        String cep,
        boolean isSeller,
        List<UUID> orderSellingId,
        List<UUID> orderBoughtId
) {
}
