 package com.mercadonosso.reviews_service.adapters.out.persistence.mongo;

 import com.mercadonosso.reviews_service.core.domain.ReviewsEntity;
 import org.springframework.stereotype.Component;

 @Component
 public class ReviewsMapper {
     public ReviewsModel toModel(ReviewsEntity domain) {
         ReviewsModel model = new ReviewsModel();
         if (domain.getId() != null) {
             model.setId(domain.getId());
         }

         model.setListingId(model.getListingId());
         model.setMessage(model.getMessage());
         model.setRating(model.getRating());
         model.setCreatedAt(model.getCreatedAt());
         model.setSellerId(model.getSellerId());
         model.setImagesUrls(model.getImagesUrls());
         return model;
     }

     public ReviewsEntity toDomain(ReviewsModel model) {
         ReviewsEntity domain = new ReviewsEntity();

         domain.setId(model.getId());
         domain.setListingId(model.getListingId());
         domain.setSellerId(model.getSellerId());
         domain.setBuyerId(model.getBuyerId());
         domain.setMessage(model.getMessage());
         domain.setRating(model.getRating());
         domain.setCreatedAt(model.getCreatedAt());
         domain.setImagesUrls(model.getImagesUrls());
         domain.setActive(true);
         return domain;
     }
 }
