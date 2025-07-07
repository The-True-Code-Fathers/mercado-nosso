package com.mercadonosso.products_service.adapters.in.web;

import com.mercadonosso.products_service.adapters.in.web.dto.CreateProductsRequest;
import com.mercadonosso.products_service.adapters.in.web.dto.ProductsResponse;
import com.mercadonosso.products_service.adapters.in.web.dto.UpdateProductsRequest;
import com.mercadonosso.products_service.core.domain.ProductsEntity;
import com.mercadonosso.products_service.core.ports.in.ProductsServicePort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProductsController {
    ProductsServicePort productsServicePort;

    public ProductsController(ProductsServicePort productsServicePort) {
        this.productsServicePort = productsServicePort;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductsResponse createProduct(@Valid
                                          @RequestBody
                                          CreateProductsRequest request) {
        ProductsEntity product = toDomain(request);
        ProductsEntity createdProduct = productsServicePort.create(product);
        return toResponse(createdProduct);
    }

    @GetMapping("/{id}")
    public ProductsResponse getProductById(@PathVariable UUID id) {
        ProductsEntity product = productsServicePort.findById(id);
        return toResponse(product);
    }

    @GetMapping
    public List<ProductsResponse> getAllProducts() {
        return productsServicePort.listAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ProductsResponse updateProduct(
            @PathVariable UUID id,
            @Valid
            @RequestBody
            UpdateProductsRequest request) {
        ProductsEntity productToUpdate = toDomain(request);
        ProductsEntity updatedProduct = productsServicePort.update(id, productToUpdate);
        return toResponse(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(
            @PathVariable UUID id) {
        productsServicePort.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ProductsEntity toDomain(CreateProductsRequest dto) {
        ProductsEntity domain = new ProductsEntity();
        domain.setSku(dto.sku());
        domain.setName(dto.name());
        domain.setDescription(dto.description());
        domain.setBrand(dto.brand());
        domain.setCategory(dto.category());
        domain.setSpecificationsText(dto.specificationsText());
        return domain;
    }

    private ProductsEntity toDomain(UpdateProductsRequest dto) {
        ProductsEntity domain = new ProductsEntity();
        domain.setSku(dto.sku());
        domain.setName(dto.name());
        domain.setDescription(dto.description());
        domain.setBrand(dto.brand());
        domain.setCategory(dto.category());
        domain.setSpecificationsText(dto.specificationsText());
        return domain;
    }

    private ProductsResponse toResponse(ProductsEntity domain) {
        return new ProductsResponse(
                domain.getId(),
                domain.getSku(),
                domain.getName(),
                domain.getSpecificationsText(),
                domain.getDescription(),
                domain.getBrand(),
                domain.getCategory(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
