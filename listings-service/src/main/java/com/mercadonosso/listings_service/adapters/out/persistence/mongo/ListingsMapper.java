package com.mercadonosso.listings_service.adapters.out.persistence.mongo;

import org.springframework.stereotype.Component;

import com.mercadonosso.listings_service.core.domain.ListingsEntity;

@Component
public class ListingsMapper {

    /**
     *
     * @param domain
     * @return returning validations and builder.
     */
    public ListingsModel toModel(ListingsEntity domain) {
        ListingsModel model = new ListingsModel();

        if (domain.getListingId() != null) {
            model.setId(domain.getListingId());
        }

        model.setSellerId(domain.getSellerId());
        model.setSku(domain.getSku());
        model.setProductRecommendation(domain.getProductRecommendation());
        model.setTitle(domain.getTitle());
        model.setDescription(domain.getDescription());
        model.setPrice(domain.getPrice());
        model.setRating(domain.getRating());
        model.setReviewsId(domain.getReviewsId());
        model.setImagesUrl(domain.getImagesUrl());
        model.setStock(domain.getStock());
        model.setActive(domain.isActive());
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
        domain.setSellerId(model.getSellerId());
        domain.setSku(model.getSku());
        domain.setProductRecommendation(model.getProductRecommendation());
        domain.setTitle(model.getTitle());
        domain.setDescription(model.getDescription());
        domain.setPrice(model.getPrice());
        domain.setRating(model.getRating());
        domain.setReviewsId(model.getReviewsId());
        domain.setImagesUrl(model.getImagesUrl());
        domain.setStock(model.getStock());
        domain.setActive(model.isActive());
        domain.setProductCondition(model.getProductCondition());

        return domain;
    }
}
