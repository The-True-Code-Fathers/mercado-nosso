package com.mercadonosso.carts_service.core.usecases;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.mercadonosso.carts_service.core.domain.CartsEntity;
import com.mercadonosso.carts_service.core.domain.exception.BusinessRuleException;
import com.mercadonosso.carts_service.core.domain.exception.CartNotFoundException;
import com.mercadonosso.carts_service.core.ports.in.CartsServicePort;
import com.mercadonosso.carts_service.core.ports.out.CartsRepositoryPort;
import com.mercadonosso.carts_service.core.ports.out.ListingDetails;
import com.mercadonosso.carts_service.core.ports.out.ListingsServicePort;

@Service
public class CartsServiceImpl implements CartsServicePort {

    public final CartsRepositoryPort cartsRepositoryPort;
    public final ListingsServicePort listingsServicePort;

    public CartsServiceImpl(CartsRepositoryPort cartsRepositoryPort, ListingsServicePort listingsServicePort) {
        this.cartsRepositoryPort = cartsRepositoryPort;
        this.listingsServicePort = listingsServicePort;
    }

    @Override
    public CartsEntity searchById(UUID userId) {
        return cartsRepositoryPort.findByUserId(userId).orElseThrow(() -> new CartNotFoundException("Cart not found!"));
    }

    @Override
    public CartsEntity add(UUID userId, UUID listingId, int quantity) {
        ListingDetails listing = listingsServicePort.findListingsById(listingId)
                .orElseThrow(() -> new BusinessRuleException("Listing not found!"));
        CartsEntity cartsEntity = cartsRepositoryPort.findByUserId(userId).orElse(new CartsEntity(userId));
        Optional<CartsEntity.CartItemEntity> existingItemOpt = cartsEntity.getItems().stream()
                .filter(item -> item.getListingId().equals(listingId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartsEntity.CartItemEntity item = existingItemOpt.get();
            int newQuantity = item.getQuantity() + quantity;
            if (listing.getStock() < newQuantity) {
                throw new BusinessRuleException("Insufficient stock.");
            }
            item.setQuantity(newQuantity);
        } else {
            if (listing.getStock() < quantity) {
                throw new BusinessRuleException("Insufficient stock.");
            }
            CartsEntity.CartItemEntity newItem = CartsEntity.CartItemEntity.builder()
                    .listingId(listing.getListingId())
                    .quantity(quantity)
                    .price(listing.getPrice())
                    .build();
            cartsEntity.getItems().add(newItem);
        }

        recalculateCart(cartsEntity);
        return cartsRepositoryPort.save(cartsEntity);
    }

    private void recalculateCart(CartsEntity cart) {
        BigDecimal subTotal = BigDecimal.ZERO;
        for (CartsEntity.CartItemEntity item : cart.getItems()) {
            item.setPrice(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            subTotal = subTotal.add(item.getPrice());
        }
        cart.setSubTotal(subTotal);
        cart.setGrandTotal(cart.getSubTotal().add(cart.getShippingPriceTotal()));
        cart.setUpdateAt(LocalDateTime.now());
    }

    @Override
    public CartsEntity remove(UUID userId, UUID listingId) {
        CartsEntity cart = cartsRepositoryPort.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found."));
        boolean itemExists = cart.getItems().stream().anyMatch(item -> item.getListingId().equals(listingId));

        if (!itemExists) {
            return cart;
        }
        cart.getItems().removeIf(item -> item.getListingId().equals(listingId));

        recalculateCart(cart);

        return cartsRepositoryPort.save(cart);
    }

    @Override
    public CartsEntity update(UUID userId, UUID listingId, int newQuantity) {
        if (newQuantity <= 0) {
            throw new BusinessRuleException(
                    "The quantity needs to be higher than zero.");
        }

        CartsEntity cart = cartsRepositoryPort.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found!"));

        CartsEntity.CartItemEntity itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getListingId().equals(listingId))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("Item " + listingId + " not found."));

        ListingDetails listing = listingsServicePort.findListingsById(listingId)
                .orElseThrow(() -> new BusinessRuleException("Listing " + listingId + " didnt exist more."));

        if (listing.getStock() < newQuantity) {
            throw new BusinessRuleException(
                    "Insufficient stock. Only " + listing.getStock() + " available.");
        }

        itemToUpdate.setQuantity(newQuantity);

        recalculateCart(cart);

        return cartsRepositoryPort.save(cart);
    }

    @Override
    public void clear(UUID userId) {

    }

}
