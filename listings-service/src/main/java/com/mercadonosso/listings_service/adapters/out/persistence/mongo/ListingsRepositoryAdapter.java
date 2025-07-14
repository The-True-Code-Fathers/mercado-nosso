package com.mercadonosso.listings_service.adapters.out.persistence.mongo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.mercadonosso.listings_service.core.domain.ListingsEntity;
import com.mercadonosso.listings_service.core.domain.enums.ProductCondition;
import com.mercadonosso.listings_service.core.ports.out.ListingsRepositoryPort;

import lombok.extern.slf4j.Slf4j;

@Component
@Repository
@Slf4j
public class ListingsRepositoryAdapter implements ListingsRepositoryPort {
    private final SpringListingsRepository mongoRepository;
    private final MongoTemplate mongoTemplate;
    private final ListingsMapper mapper;

    public ListingsRepositoryAdapter(SpringListingsRepository springListingsRepository,
            MongoTemplate mongoTemplate,
            ListingsMapper mapper) {
        this.mongoRepository = springListingsRepository;
        this.mongoTemplate = mongoTemplate;
        this.mapper = mapper;
    }

    @Override
    public ListingsEntity save(ListingsEntity listingsEntity) {
        ListingsModel model = mapper.toModel(listingsEntity);
        ListingsModel savedModel = mongoRepository.save(model);
        return mapper.toDomain(savedModel);
    }

    @Override
    public Optional<ListingsEntity> findById(ObjectId id) {
        Optional<ListingsModel> modelOptional = mongoRepository.findById(id);
        return modelOptional.map(mapper::toDomain);
    }

    @Override
    public List<ListingsEntity> listAll() {
        List<ListingsModel> models = mongoRepository.findAll();
        return models.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(ListingsEntity listingsEntity) {
        ListingsModel model = mapper.toModel(listingsEntity);
        mongoRepository.delete(model);
    }

    @Override
    public List<ListingsEntity> searchListings(String partialName, ProductCondition productCondition,
            BigDecimal minPrice, BigDecimal maxPrice) {

        Criteria criteria = Criteria.where("active").is(true);

        if (partialName != null && !partialName.trim().isEmpty()) {
            Criteria textCriteria = new Criteria().orOperator(
                    Criteria.where("title").regex(partialName, "i"),
                    Criteria.where("description").regex(partialName, "i"));
            criteria = criteria.andOperator(textCriteria);
        }

        if (productCondition != null) {
            criteria = criteria.and("productCondition").is(productCondition);
        }

        if (minPrice != null) {
            criteria = criteria.and("price").gte(minPrice);
        }
        if (maxPrice != null) {
            criteria = criteria.and("price").lte(maxPrice);
        }

        Query query = new Query(criteria);
        List<ListingsModel> models = mongoTemplate.find(query, ListingsModel.class);

        log.info("Search Listings - Criteria: {}, Found: {}", criteria, models);

        return models.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
