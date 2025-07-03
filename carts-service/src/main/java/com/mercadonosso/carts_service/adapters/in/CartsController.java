package com.mercadonosso.carts_service.adapters.in;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.mercadonosso.carts_service.adapters.in.web.dto.UpdateItemQuantityRequest;
import com.mercadonosso.carts_service.core.domain.CartsEntity;
import com.mercadonosso.carts_service.core.ports.in.CartsServicePort;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/carts")
public class CartsController {

    @Autowired
    private CartsServicePort cartsServicePort;

    @GetMapping
    public ResponseEntity<CartsResponse> getCartForCurrentUser(@RequestHeader("X-User-Id") String userIdString) {
        UUID userId = UUID.fromString(userIdString);
        CartsEntity cart = cartsServicePort.findById(userId);
        return ResponseEntity.ok(convertToResponse(cart));
    }

    @PostMapping("/items")
    public ResponseEntity<CartsResponse> addItemToCart(@RequestHeader("X-User-Id") String userIdString,
            @Valid @RequestBody AddItemRequest request) {
        UUID userId = UUID.fromString(userIdString);
        CartsEntity updatedCart = cartsServicePort.add(userId, request.getListingId(), request.getQuantity());

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(updatedCart));
    }

    @PutMapping
    public ResponseEntity<CartsResponse> updateItemQuantity(@RequestHeader("X-User-Id") String userIdString,
            @Valid @RequestBody UpdateItemQuantityRequest request) {
        UUID userId = UUID.fromString(userIdString);
        CartsEntity updatedCart = cartsServicePort.update(userId, request.getListingId(), request.getQuantity());

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(updatedCart));
    }

    @DeleteMapping
    public ResponseEntity<CartsResponse> clearCart(@RequestHeader("X-User-Id") String userIdString) {
        UUID userId = UUID.fromString(userIdString);
        cartsServicePort.requestClear(userId);
        return ResponseEntity.accepted().build();
    }

    private CartsResponse convertToResponse(CartsEntity cartsEntity) {
        if (cartsEntity == null) {
            return new CartsResponse();
        }

        BigDecimal subtotal = cartsEntity.getItems().stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
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
                        cartsEntity.getItems().stream().map(item -> CartsResponse.CartsItemResponse.builder()
                                .listingId(item.getListingId())
                                .quantity(item.getQuantity())
                                .price(item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                                .build()).collect(Collectors.toList()))
                .build();
    }

}
