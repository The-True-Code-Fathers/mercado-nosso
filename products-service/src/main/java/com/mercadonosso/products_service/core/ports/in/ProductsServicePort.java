package com.mercadonosso.products_service.core.ports.in;

import com.mercadonosso.products_service.core.domain.ProductsEntity;
import org.springframework.stereotype.Repository;
import scala.collection.immutable.List;

import java.util.UUID;

@Repository
public interface ProductsServicePort {
    ProductsEntity create(ProductsEntity productsEntity);
    ProductsEntity update(UUID id, ProductsEntity productsEntity);
    void delete(UUID id);
    List<ProductsEntity> listAll();
    ProductsEntity findById(UUID id);
}
