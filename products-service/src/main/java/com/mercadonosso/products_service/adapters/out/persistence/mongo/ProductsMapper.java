package com.mercadonosso.products_service.adapters.out.persistence.mongo;

import org.springframework.stereotype.Component;

import com.mercadonosso.products_service.core.domain.ProductsEntity;

@Component
public class ProductsMapper {
    public ProductsModel toModel(ProductsEntity domain) {
        if (domain == null)
            return null;
        ProductsModel model = new ProductsModel();
        if (domain.getId() != null) {
            model.setId(domain.getId());
        }
        model.setSku(domain.getSku());
        model.setName(domain.getName());
        model.setSpecifications(domain.getSpecificationsText());
        model.setBrand(domain.getBrand());
        model.setCategory(domain.getCategory());
        model.setDescription(domain.getDescription());
        model.setCreatedAt(domain.getCreatedAt());
        model.setUpdatedAt(domain.getUpdatedAt());
        return model;
    }

    public ProductsEntity toDomain(ProductsModel model) {
        if (model == null)
            return null;
        ProductsEntity domain = new ProductsEntity();
        domain.setId(model.getId());
        domain.setSku(model.getSku());
        domain.setName(model.getName());
        domain.setSpecificationsText(model.getSpecifications());
        domain.setBrand(model.getBrand());
        domain.setCategory(model.getCategory());
        domain.setDescription(model.getDescription());
        domain.setCreatedAt(model.getCreatedAt());
        domain.setUpdatedAt(model.getUpdatedAt());
        return domain;
    }
}
