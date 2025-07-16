package com.mercadonosso.reviews_service.adapters.out.persistence.mongo;

import com.mercadonosso.reviews_service.core.domain.ReviewsEntity;
import com.mercadonosso.reviews_service.core.ports.out.ReviewsRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ReviewsRepositoryAdapter implements ReviewsRepositoryPort {
    private final SpringMongoReviewsRepository mongoRepository;
    private final ReviewsMapper mapper;

    public ReviewsRepositoryAdapter(SpringMongoReviewsRepository mongoRepository, ReviewsMapper mapper) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }

    @Override
    public ReviewsEntity save(ReviewsEntity reviewsEntity) {
        ReviewsModel model = mapper.toModel(reviewsEntity);
        ReviewsModel savedModel = mongoRepository.save(model);
        return mapper.toDomain(savedModel);
    }

    @Override
    public void delete(ReviewsEntity reviewsEntity) {
        ReviewsModel model = mapper.toModel(reviewsEntity);
        mongoRepository.delete(model);
    }

    @Override
    public List<ReviewsEntity> listAll() {
        List<ReviewsModel> reviewsModels = mongoRepository.findAll();
        return reviewsModels.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ReviewsEntity> findById(UUID id) {
        Optional<ReviewsModel> modelOptional = mongoRepository.findById(id);
        return modelOptional.map(mapper::toDomain);
    }

    @Override
    public List<ReviewsEntity> findByListingId(String listingId) {
        List<ReviewsModel> reviewsModels = mongoRepository.findByListingId(listingId);
        return reviewsModels.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
