package com.mercadonosso.products_service.core.usecases;

import com.mercadonosso.products_service.core.domain.ProductsEntity;
import com.mercadonosso.products_service.core.domain.exception.BusinessRuleException;
import com.mercadonosso.products_service.core.domain.exception.ProductsNotFoundException;
import com.mercadonosso.products_service.core.ports.in.ProductsServicePort;
import com.mercadonosso.products_service.core.ports.out.ProductsRepositoryPort;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class ProductsServiceImpl implements ProductsServicePort {
    private final ProductsRepositoryPort productsRepositoryPort;
    private final Validator validator;

    public ProductsServiceImpl(ProductsRepositoryPort productsRepositoryPort, Validator validator) {
        this.productsRepositoryPort = productsRepositoryPort;
        this.validator = validator;
    }

    @Override
    @Transactional
    public ProductsEntity create(ProductsEntity productData) {
        validate(productData);
        String existingSku = productData.getSku();

        ProductsEntity newProduct = ProductsEntity.builder()
                .sku(productData.getSku())
                .name(productData.getName())
                .specificationsText(productData.getSpecificationsText())
                .brand(productData.getBrand())
                .category(productData.getCategory())
                .description(productData.getDescription())
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .build();

        Optional<ProductsEntity> existingProduct = productsRepositoryPort.findBySku(existingSku);

        if (existingProduct.isPresent()) {
            throw new BusinessRuleException("Product with id " + existingSku + " already exists");
        }

        return productsRepositoryPort.save(newProduct);
    }

    @Override
    @Transactional
    public ProductsEntity update(UUID id, ProductsEntity productWithNewData) {
        validate(productWithNewData);
        ProductsEntity existingProduct = this.findById(id);

        existingProduct.setName(productWithNewData.getName());
        existingProduct.setSku(productWithNewData.getSku());
        existingProduct.setSpecificationsText(productWithNewData.getSpecificationsText());
        existingProduct.setBrand(productWithNewData.getBrand());
        existingProduct.setCategory(productWithNewData.getCategory());
        existingProduct.setDescription(productWithNewData.getDescription());
        existingProduct.setUpdatedAt(LocalDateTime.now());

        return productsRepositoryPort.save(existingProduct);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ProductsEntity productToDelete = this.findById(id);
        productsRepositoryPort.delete(productToDelete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductsEntity> listAll() {
        return productsRepositoryPort.listAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductsEntity findById(UUID id) {
        return productsRepositoryPort.findById(id)
                .orElseThrow(() -> new ProductsNotFoundException("Produto com ID " + id + " não encontrado."));
    }

    @Override
    public ProductsEntity findBySku(String sku) {
        return productsRepositoryPort.findBySku(sku)
                .orElseThrow(() -> new ProductsNotFoundException("Product with SKU" + sku + " not found"));
    }

    private void validate(ProductsEntity productsEntity) {
        Set<ConstraintViolation<ProductsEntity>> violations = validator.validate(productsEntity);
        if (!violations.isEmpty()) {
            throw new BusinessRuleException(violations.iterator().next().getMessage());
        }
    }
}
