package com.mercadonosso.listings_service.adapters.out.persistence.mongo;

import org.springframework.stereotype.Component;

import com.mercadonosso.listings_service.core.domain.ListingsEntity;

@Component
public class ListingsMapper {
    ListingsModel model = new ListingsModel();

    /**
     *
     * @param domain
     * @return returning validations and builder.
     */
    public ListingsModel toModel(ListingsEntity domain) {
        if (domain.getListingId() != null) {
            model.setId(domain.getListingId());
        }

        model.setProductId(domain.getProductSku());
        model.setSellerId(domain.getSellerId());
        model.setTitle(domain.getTitle());
        model.setDescription(domain.getDescription());
        model.setPrice(domain.getPrice());
        model.setStock(domain.getStock());
        model.setActive(domain.isActive());
        model.setCreatedAt(domain.getCreatedAt());
        model.setProductCondition(domain.getProductCondition());

        return model;
    }

    /**
     *
     * @param model
     * @return returning as a param model and creating a domain settings attributes
     */
    public ListingsEntity toDomain(ListingsModel model) {
        ListingsEntity domain = new ListingsEntity();

        domain.setListingId(model.getId());
        domain.setProductSku(model.getProductId());
        domain.setSellerId(model.getSellerId());
        domain.setTitle(model.getTitle());
        domain.setDescription(model.getDescription());
        domain.setPrice(model.getPrice());
        domain.setStock(model.getStock());
        domain.setActive(model.isActive());
        domain.setCreatedAt(model.getCreatedAt());
        domain.setProductCondition(model.getProductCondition());

        return domain;
    }
}
