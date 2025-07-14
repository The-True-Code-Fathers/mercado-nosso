package com.mercadonosso.carts_service.adapters.in;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.mercadonosso.carts_service.adapters.in.web.dto.AddItemRequest;
import com.mercadonosso.carts_service.adapters.in.web.dto.CartsResponse;
import com.mercadonosso.carts_service.adapters.in.web.dto.RemoveListingRequest;
import com.mercadonosso.carts_service.adapters.in.web.dto.UpdateItemQuantityRequest;
import com.mercadonosso.carts_service.core.domain.CartsEntity;
import com.mercadonosso.carts_service.core.ports.in.CartsServicePort;

import jakarta.validation.Valid;

@RestController
public class CartsController {

    private static final Logger logger = LoggerFactory.getLogger(CartsController.class);
    private final CartsServicePort cartsServicePort;

    public CartsController(CartsServicePort cartsServicePort) {
        this.cartsServicePort = cartsServicePort;
    }

    @GetMapping
    public ResponseEntity<CartsResponse> getCartForCurrentUser(@RequestHeader("X-User-Id") String userIdString) {
        logger.info("GET /carts - Iniciando busca do carrinho para usuário: {}", userIdString);
        try {
            UUID userId = UUID.fromString(userIdString);
            logger.debug("GET /carts - UUID parseado com sucesso: {}", userId);

            CartsEntity cart = cartsServicePort.findById(userId);
            logger.debug("GET /carts - Carrinho encontrado: {}", cart != null ? cart.getId() : "null");

            CartsResponse response = convertToResponse(cart);
            logger.info("GET /carts - Carrinho retornado com sucesso para usuário: {}", userIdString);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("GET /carts - Erro ao parsear UUID do usuário: {}", userIdString, e);
            throw e;
        } catch (Exception e) {
            logger.error("GET /carts - Erro inesperado ao buscar carrinho para usuário: {}", userIdString, e);
            throw e;
        }
    }

    @PostMapping("/items")
    public ResponseEntity<CartsResponse> addItemToCart(
            @RequestHeader("X-User-Id") String userIdString,
            @Valid @RequestBody AddItemRequest request) {
        logger.info("POST /items - Adicionando item ao carrinho para usuário: {}, listingId: {}, quantidade: {}",
                userIdString, request.getListingId(), request.getQuantity());
        try {
            UUID userId = UUID.fromString(userIdString);
            logger.debug("POST /items - UUID parseado com sucesso: {}", userId);

            CartsEntity updatedCart = cartsServicePort.create(userId, request.getListingId(), request.getQuantity());
            logger.debug("POST /items - Item adicionado com sucesso, carrinho ID: {}", updatedCart.getId());

            CartsResponse response = convertToResponse(updatedCart);
            logger.info("POST /items - Item adicionado ao carrinho com sucesso para usuário: {}", userIdString);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.error("POST /items - Erro ao parsear UUID do usuário: {}", userIdString, e);
            throw e;
        } catch (Exception e) {
            logger.error("POST /items - Erro inesperado ao adicionar item ao carrinho para usuário: {}", userIdString,
                    e);
            throw e;
        }
    }

    @PutMapping("/items")
    public ResponseEntity<CartsResponse> updateItemQuantity(
            @RequestHeader("X-User-Id") String userIdString,
            @Valid @RequestBody UpdateItemQuantityRequest request) {
        logger.info(
                "PUT /items - Atualizando quantidade do item no carrinho para usuário: {}, listingId: {}, nova quantidade: {}",
                userIdString, request.getListingId(), request.getQuantity());
        try {
            UUID userId = UUID.fromString(userIdString);
            logger.debug("PUT /items - UUID parseado com sucesso: {}", userId);

            CartsEntity updatedCart = cartsServicePort.update(userId, request.getListingId(), request.getQuantity());
            logger.debug("PUT /items - Quantidade atualizada com sucesso, carrinho ID: {}", updatedCart.getId());

            CartsResponse response = convertToResponse(updatedCart);
            logger.info("PUT /items - Quantidade do item atualizada com sucesso para usuário: {}", userIdString);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("PUT /items - Erro ao parsear UUID do usuário: {}", userIdString, e);
            throw e;
        } catch (Exception e) {
            logger.error("PUT /items - Erro inesperado ao atualizar item do carrinho para usuário: {}", userIdString,
                    e);
            throw e;
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") String userIdString) {
        logger.info("DELETE /carts - Limpando carrinho para usuário: {}", userIdString);
        try {
            UUID userId = UUID.fromString(userIdString);
            logger.debug("DELETE /carts - UUID parseado com sucesso: {}", userId);

            cartsServicePort.requestClear(userId);
            logger.info("DELETE /carts - Carrinho limpo com sucesso para usuário: {}", userIdString);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("DELETE /carts - Erro ao parsear UUID do usuário: {}", userIdString, e);
            throw e;
        } catch (Exception e) {
            logger.error("DELETE /carts - Erro inesperado ao limpar carrinho para usuário: {}", userIdString, e);
            throw e;
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeItemCart(@RequestHeader("X-User-Id") String userIdString,
            @Valid @RequestBody RemoveListingRequest request) {
        logger.info("DELETE /remove - Removendo itens do carrinho para usuário: {}, listingIds: {}",
                userIdString, request.getListingsIds());
        try {
            UUID userId = UUID.fromString(userIdString);
            logger.debug("DELETE /remove - UUID parseado com sucesso: {}", userId);

            cartsServicePort.requestRemove(userId, request.getListingsIds());
            logger.info("DELETE /remove - Itens removidos com sucesso do carrinho para usuário: {}", userIdString);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.error("DELETE /remove - Erro ao parsear UUID do usuário: {}", userIdString, e);
            throw e;
        } catch (Exception e) {
            logger.error("DELETE /remove - Erro inesperado ao remover itens do carrinho para usuário: {}", userIdString,
                    e);
            throw e;
        }
    }

    private CartsResponse convertToResponse(CartsEntity cartsEntity) {
        logger.debug("Convertendo CartsEntity para CartsResponse: {}",
                cartsEntity != null ? cartsEntity.getId() : "null");
        try {
            if (cartsEntity == null) {
                logger.debug("CartsEntity é null, retornando CartsResponse vazio");
                return new CartsResponse();
            }

            List<CartsEntity.CartItemEntity> items = cartsEntity.getItems() != null ? cartsEntity.getItems()
                    : Collections.emptyList();
            logger.debug("Número de itens no carrinho: {}", items.size());

            BigDecimal subtotal = items.stream()
                    .map(item -> item.getPrice())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal shippingPrice = cartsEntity.getShippingPriceTotal() != null ? cartsEntity.getShippingPriceTotal()
                    : BigDecimal.ZERO;
            BigDecimal total = subtotal.add(shippingPrice);

            logger.debug("Subtotal: {}, Shipping: {}, Total: {}", subtotal, shippingPrice, total);

            CartsResponse response = CartsResponse.builder()
                    .id(cartsEntity.getId())
                    .userId(cartsEntity.getUserId())
                    .subTotal(subtotal)
                    .shippingPriceTotal(shippingPrice)
                    .grandTotal(total)
                    .items(
                            items.stream().map(item -> CartsResponse.CartsItemResponse.builder()
                                    .listingId(item.getListingId() != null ? item.getListingId().toHexString() : null)
                                    .quantity(item.getQuantity())
                                    .price(item.getPrice())
                                    .shippingPrice(item.getShippingPrice())
                                    .build()).collect(Collectors.toList()))
                    .build();

            logger.debug("CartsResponse criado com sucesso com {} itens", response.getItems().size());
            return response;
        } catch (Exception e) {
            logger.error("Erro ao converter CartsEntity para CartsResponse", e);
            throw e;
        }
    }
}