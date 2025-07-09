package com.mercadonosso.carts_service.core.usecases;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
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
    public final KafkaTemplate<String, String> kafkaTemplate;
    public final String clearCartTopic;

    public CartsServiceImpl(CartsRepositoryPort cartsRepositoryPort, ListingsServicePort listingsServicePort,
            KafkaTemplate<String, String> kafkaTemplate, @Value("${topics.cart-clear.name}") String clearCartTopic) {
        this.cartsRepositoryPort = cartsRepositoryPort;
        this.listingsServicePort = listingsServicePort;
        this.kafkaTemplate = kafkaTemplate;
        this.clearCartTopic = clearCartTopic;
    }

    @Override
    public CartsEntity findById(UUID userId) {
        return cartsRepositoryPort.findByUserId(userId).orElseThrow(() -> new CartNotFoundException("Cart not found!"));
    }

    @Override
    public CartsEntity create(UUID userId, UUID listingId, int quantity) {
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
                    .shippingPrice(listing.getPrice().multiply(new BigDecimal(quantity)).multiply(new BigDecimal(0.05)))
                    .build();
            cartsEntity.getItems().add(newItem);
            cartsEntity.setShippingPriceTotal(cartsEntity.getShippingPriceTotal().add(newItem.getShippingPrice()));
        }
        recalculateCart(cartsEntity, listing);
        return cartsRepositoryPort.save(cartsEntity);
    }

    private void recalculateCart(CartsEntity cart, ListingDetails listing) {
        BigDecimal subTotal = BigDecimal.ZERO;
        // Define the scale and rounding mode for currency calculations
        int currencyScale = 2;
        RoundingMode roundingMode = RoundingMode.HALF_UP;

        for (CartsEntity.CartItemEntity item : cart.getItems()) {
            if (listing.getListingId().equals(item.getListingId())) {
                BigDecimal itemTotal = listing.getPrice().multiply(new BigDecimal(item.getQuantity()));
                item.setPrice(itemTotal.setScale(currencyScale, roundingMode));

                BigDecimal shippingPrice = itemTotal.multiply(new BigDecimal("0.05"));
                item.setShippingPrice(shippingPrice.setScale(currencyScale, roundingMode));

                subTotal = subTotal.add(item.getPrice());
            }
        }

        BigDecimal totalShipping = cart.getItems().stream()
                .map(CartsEntity.CartItemEntity::getShippingPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setSubTotal(subTotal.setScale(currencyScale, roundingMode));
        cart.setShippingPriceTotal(totalShipping.setScale(currencyScale, roundingMode));
        cart.setGrandTotal(cart.getSubTotal().add(cart.getShippingPriceTotal()));
        cart.setUpdateAt(LocalDateTime.now());
    }

    @Override
    public CartsEntity remove(UUID userId, UUID listingId) {

        ListingDetails listing = listingsServicePort.findListingsById(listingId)
                .orElseThrow(() -> new BusinessRuleException("Listing " + listingId + " didnt exist more."));

        CartsEntity cart = cartsRepositoryPort.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found."));
        boolean itemExists = cart.getItems().stream().anyMatch(item -> item.getListingId().equals(listingId));

        if (!itemExists) {
            return cart;
        }
        cart.getItems().removeIf(item -> item.getListingId().equals(listingId));

        recalculateCart(cart, listing);

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

        recalculateCart(cart, listing);

        return cartsRepositoryPort.save(cart);
    }

    @Override
    public void requestClear(UUID userId) {
        kafkaTemplate.send(clearCartTopic, userId.toString());
    }

    @KafkaListener(topics = "${topics.cart-clear.name}", groupId = "${spring.kafka.consumer.group-id}")
    @Override
    public void processClear(UUID userId) {
        try {
            cartsRepositoryPort.delete(userId);
        } catch (Exception e) {
            e.getMessage();
        }
    }

}
