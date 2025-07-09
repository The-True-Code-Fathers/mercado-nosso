package com.mercadonosso.reviews_service.adapters.out.persistence.mongo;

import org.springframework.stereotype.Component;

import com.mercadonosso.reviews_service.core.domain.ReviewsEntity;

@Component
public class ReviewsMapper {
    public ReviewsModel toModel(ReviewsEntity domain) {
        if (domain == null)
            return null;
        ReviewsModel model = new ReviewsModel();

        if (domain.getId() != null) {
            model.setId(domain.getId());
        }

        model.setListingId(domain.getListingId());
        model.setBuyerId(domain.getBuyerId());
        model.setSellerId(domain.getSellerId());
        model.setRating(domain.getRating());
        model.setMessage(domain.getMessage());
        model.setImagesUrls(domain.getImagesUrls());
        model.setCreatedAt(domain.getCreatedAt());
        model.setActive(domain.isActive());
        return model;
    }

    public ReviewsEntity toDomain(ReviewsModel model) {
        if (model == null)
            return null;
        ReviewsEntity domain = new ReviewsEntity();

        domain.setId(model.getId());
        domain.setListingId(model.getListingId());
        domain.setBuyerId(model.getBuyerId());
        domain.setSellerId(model.getSellerId());
        domain.setRating(model.getRating());
        domain.setMessage(model.getMessage());
        domain.setImagesUrls(model.getImagesUrls());
        domain.setCreatedAt(model.getCreatedAt());
        domain.setActive(model.isActive());
        return domain;
    }
}