package com.mercadonosso.listings_service.adapters.in;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "http://localhost:4200")
public class ListingsController {
    private final ListingsServicePort listingsServicePort;

    public ListingsController(ListingsServicePort listingsServicePort) {
        this.listingsServicePort = listingsServicePort;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListingResponse createListing(@RequestBody CreatingListingRequest request) {
        ListingsEntity listingsEntity = new ListingsEntity();
        listingsEntity.setProductSku(request.productId());
        listingsEntity.setSellerId(request.sellerId());
        listingsEntity.setTitle(request.title());
        listingsEntity.setDescription(request.description());
        listingsEntity.setPrice(request.price());
        listingsEntity.setStock(request.stock());
        listingsEntity.setProductCondition(request.productCondition());
        ListingsEntity createdListingsEntity = listingsServicePort.create(listingsEntity);

        return toResponse(createdListingsEntity);
    }

    @GetMapping("/{id}")
    public ListingResponse getListingById(@PathVariable UUID id) {
        ListingsEntity listingsEntity = listingsServicePort.findById(id);
        return toResponse(listingsEntity);
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
                listingsEntity.getListingId(),
                listingsEntity.getTitle(),
                listingsEntity.getDescription(),
                listingsEntity.getPrice(),
                listingsEntity.getStock(),
                listingsEntity.isActive(),
                listingsEntity.getProductCondition(),
                listingsEntity.getCreatedAt());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListingById(@PathVariable UUID id) {
        ListingsEntity listingToDelete = listingsServicePort.findById(id);
        listingsServicePort.delete(listingToDelete);

        return ResponseEntity.noContent().build();
    }
}