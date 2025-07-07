package com.mercadonosso.products_service.adapters.out.persistence.mongo;

import com.mercadonosso.products_service.core.domain.ProductsEntity;
import com.mercadonosso.products_service.core.ports.out.ProductsRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ProductsRepositoryAdapter implements ProductsRepositoryPort {
    private final SpringMongoProductsRepository mongoRepository;
    private final ProductsMapper mapper;

    public ProductsRepositoryAdapter(SpringMongoProductsRepository mongoRepository, ProductsMapper mapper) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }

    @Override
    public ProductsEntity save(ProductsEntity product) {
        ProductsModel model = mapper.toModel(product);
        ProductsModel savedModel = mongoRepository.save(model);
        return mapper.toDomain(savedModel);
    }

    @Override
    public ProductsEntity update(ProductsEntity productsEntity) {
        return null;
    }

    @Override
    public void delete(ProductsEntity product) {
        mongoRepository.deleteById(product.getId());
    }

    @Override
    public Optional<ProductsEntity> findById(UUID id) {
        return mongoRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<ProductsEntity> listAll() {
        return mongoRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
