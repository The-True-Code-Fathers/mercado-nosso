package com.mercadonosso.products_service.core.ports.out;

import com.mercadonosso.products_service.core.domain.ProductsEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductsRepositoryPort {
    ProductsEntity save(ProductsEntity productsEntity);
    ProductsEntity update(ProductsEntity productsEntity);
    void delete(ProductsEntity productsEntity);
    Optional<ProductsEntity> findById(UUID id);
    List<ProductsEntity> listAll();
}
