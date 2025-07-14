package com.mercadonosso.listings_service.adapters.in;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mercadonosso.listings_service.adapters.in.web.dto.CreatingListingRequest;
import com.mercadonosso.listings_service.adapters.in.web.dto.ListingResponse;
import com.mercadonosso.listings_service.core.domain.ListingsEntity;
import com.mercadonosso.listings_service.core.ports.in.ListingsServicePort;

@RestController
public class ListingsController {

    private static final Logger logger = LoggerFactory.getLogger(ListingsController.class);
    private final ListingsServicePort listingsServicePort;

    public ListingsController(ListingsServicePort listingsServicePort) {
        this.listingsServicePort = listingsServicePort;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListingResponse createListing(@RequestBody CreatingListingRequest request) {
        ListingsEntity listingsEntity = new ListingsEntity();
        listingsEntity.setProductSku(String.valueOf(request.productId()));
        listingsEntity.setSellerId(String.valueOf(request.sellerId()));
        listingsEntity.setTitle(request.title());
        listingsEntity.setDescription(request.description());
        listingsEntity.setPrice(request.price());
        listingsEntity.setStock(request.stock());
        listingsEntity.setProductCondition(request.productCondition());
        ListingsEntity createdListingsEntity = listingsServicePort.create(listingsEntity);

        return toResponse(createdListingsEntity);
    }

    @GetMapping("/{id}")
    public ListingResponse getListingById(@PathVariable ObjectId id) {
        logger.info("GET /{} - Recebida requisição para buscar listing com ID: {}", id, id);
        logger.info("GET /{} - ObjectId recebido como string: {}", id, id.toHexString());

        try {
            ListingsEntity listingsEntity = listingsServicePort.findById(id);
            logger.info("GET /{} - Listing encontrado no banco: {}", id,
                    listingsEntity != null ? listingsEntity.getListingId() : "null");

            if (listingsEntity == null) {
                logger.warn("GET /{} - Listing não encontrado no banco de dados", id);
            }

            ListingResponse response = toResponse(listingsEntity);
            logger.info("GET /{} - Resposta gerada: {}", id, response);
            return response;
        } catch (Exception e) {
            logger.error("GET /{} - Erro ao buscar listing", id, e);
            throw e;
        }
    }

    @GetMapping
    public List<ListingResponse> getAllListings() {
        List<ListingsEntity> listingsEntities = listingsServicePort.listAll();
        return listingsEntities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ListingResponse toResponse(ListingsEntity listingsEntity) {
        return new ListingResponse(
                listingsEntity.getListingId() != null ? listingsEntity.getListingId().toHexString() : null,
                listingsEntity.getTitle(),
                listingsEntity.getDescription(),
                listingsEntity.getPrice(),
                listingsEntity.getStock(),
                listingsEntity.isActive(),
                listingsEntity.getProductCondition(),
                listingsEntity.getCreatedAt());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListingById(@PathVariable ObjectId id) {
        ListingsEntity listingToDelete = listingsServicePort.findById(id);
        listingsServicePort.delete(listingToDelete);

        return ResponseEntity.noContent().build();
    }
}