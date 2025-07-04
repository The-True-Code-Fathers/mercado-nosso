package com.mercadonosso.carts_service.adapters.out.persistence.mongo;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.mercadonosso.carts_service.core.domain.CartsEntity;
import com.mercadonosso.carts_service.core.ports.out.CartsRepositoryPort;

@Component
public class CartsRepositoryAdapter implements CartsRepositoryPort {

    private final SpringCartRepository springCartRepository;

    public CartsRepositoryAdapter(SpringCartRepository springCartRepository) {
        this.springCartRepository = springCartRepository;
    }

    @Override
    public Optional<CartsEntity> findByUserId(UUID userId) {
        Optional<CartsModel> cartModelOptional = springCartRepository.findByUserId(userId);
        return cartModelOptional.map(CartsMapper::toDomain);
    }

    @Override
    public CartsEntity save(CartsEntity cart) {
        CartsModel modelToSave = CartsMapper.toModel(cart);

        CartsModel savedModel = springCartRepository.save(modelToSave);
        return CartsMapper.toDomain(savedModel);
    }

    @Override
    public void delete(UUID userId) {
        var cart = springCartRepository.findByUserId(userId).get();
        springCartRepository.delete(cart);
    }

}
