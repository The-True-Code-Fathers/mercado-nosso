package com.mercadonosso.carts_service.adapters.in;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mercadonosso.carts_service.adapters.in.web.dto.AddItemRequest;
import com.mercadonosso.carts_service.adapters.in.web.dto.CartsResponse;
import com.mercadonosso.carts_service.adapters.in.web.dto.RemoveListingRequest;
import com.mercadonosso.carts_service.adapters.in.web.dto.UpdateItemQuantityRequest;
import com.mercadonosso.carts_service.core.domain.CartsEntity;
import com.mercadonosso.carts_service.core.ports.in.CartsServicePort;

import jakarta.validation.Valid;

@RestController
@RequestMapping
public class CartsController {

    private final CartsServicePort cartsServicePort;

    public CartsController(CartsServicePort cartsServicePort) {
        this.cartsServicePort = cartsServicePort;
    }

    @GetMapping
    public ResponseEntity<CartsResponse> getCartForCurrentUser(@RequestHeader("X-User-Id") String userIdString) {
        UUID userId = UUID.fromString(userIdString);
        CartsEntity cart = cartsServicePort.findById(userId);
        return ResponseEntity.ok(convertToResponse(cart));
    }

    @PostMapping("/items")
    public ResponseEntity<CartsResponse> addItemToCart(
            @RequestHeader("X-User-Id") String userIdString,
            @Valid @RequestBody AddItemRequest request) {
        UUID userId = UUID.fromString(userIdString);
        CartsEntity updatedCart = cartsServicePort.create(userId, request.getListingId(), request.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(updatedCart));
    }

    @PutMapping("/items")
    public ResponseEntity<CartsResponse> updateItemQuantity(
            @RequestHeader("X-User-Id") String userIdString,
            @Valid @RequestBody UpdateItemQuantityRequest request) {
        UUID userId = UUID.fromString(userIdString);
        CartsEntity updatedCart = cartsServicePort.update(userId, request.getListingId(), request.getQuantity());
        return ResponseEntity.ok(convertToResponse(updatedCart));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") String userIdString) {
        UUID userId = UUID.fromString(userIdString);
        cartsServicePort.requestClear(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeItemCart(@RequestHeader("X-User-Id") String userIdString, @Valid @RequestBody RemoveListingRequest request) {
        UUID userId = UUID.fromString(userIdString);
        cartsServicePort.requestRemove(userId, request.getListingsIds());
        return ResponseEntity.noContent().build();
    }

    private CartsResponse convertToResponse(CartsEntity cartsEntity) {
        if (cartsEntity == null) {
            return new CartsResponse();
        }

        List<CartsEntity.CartItemEntity> items = cartsEntity.getItems() != null ? cartsEntity.getItems()
                : Collections.emptyList();

        BigDecimal subtotal = items.stream()
                .map(item -> item.getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingPrice = cartsEntity.getShippingPriceTotal() != null ? cartsEntity.getShippingPriceTotal()
                : BigDecimal.ZERO;
        BigDecimal total = subtotal.add(shippingPrice);

        return CartsResponse.builder()
                .id(cartsEntity.getId())
                .userId(cartsEntity.getUserId())
                .subTotal(subtotal)
                .shippingPriceTotal(shippingPrice)
                .grandTotal(total)
                .items(
                        items.stream().map(item -> CartsResponse.CartsItemResponse.builder()
                                .listingId(item.getListingId())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .shippingPrice(item.getShippingPrice())
                                .build()).collect(Collectors.toList()))
                .build();
    }
}