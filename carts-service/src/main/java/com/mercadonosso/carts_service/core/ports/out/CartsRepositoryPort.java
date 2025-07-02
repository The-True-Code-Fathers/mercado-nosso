package com.mercadonosso.carts_service.core.ports.out;

import java.util.Optional;
import java.util.UUID;

import com.mercadonosso.carts_service.core.domain.CartsEntity;

public interface CartsRepositoryPort {
    Optional<CartsEntity> findByUserId(UUID userId);
    CartsEntity save(CartsEntity cart);
    void delete(UUID userId);
}
