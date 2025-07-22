package com.mercadonosso.listings_service.core.usecases;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.bson.Document;

import com.mercadonosso.listings_service.core.domain.ListingsEntity;
import com.mercadonosso.listings_service.core.domain.PagedResult;
import com.mercadonosso.listings_service.core.domain.Pagination;
import com.mercadonosso.listings_service.core.domain.enums.ProductCondition;
import com.mercadonosso.listings_service.core.domain.enums.SearchOrdering;
import com.mercadonosso.listings_service.core.domain.exception.BusinessRuleException;
import com.mercadonosso.listings_service.core.domain.exception.ListingsNotFoundException;
import com.mercadonosso.listings_service.core.ports.in.ListingsServicePort;
import com.mercadonosso.listings_service.core.ports.out.ListingsRepositoryPort;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class ListingsServiceImpl implements ListingsServicePort {
    private final ListingsRepositoryPort listingsRepositoryPort;
    private final Validator validator;
    private final MongoTemplate mongoTemplate;


    public ListingsServiceImpl(ListingsRepositoryPort listingsRepositoryPort, Validator validator, MongoTemplate mongoTemplate) {
        this.listingsRepositoryPort = listingsRepositoryPort;
        this.mongoTemplate = mongoTemplate;
        this.validator = validator;
    }

    public ListingsEntity create(ListingsEntity listingsEntity) {
        validateListing(listingsEntity);
        listingsEntity.setId(new ObjectId());
        listingsEntity.setUpdatedAt(LocalDateTime.now());
        listingsEntity.setActive(true);
        return listingsRepositoryPort.save(listingsEntity);
    }

    @Override
    public ListingsEntity update(ObjectId id, ListingsEntity newListingData) {
        ListingsEntity existingListing = this.findById(id);

        validateListing(newListingData);

        existingListing.setTitle(newListingData.getTitle());
        existingListing.setDescription(newListingData.getDescription());
        existingListing.setPrice(newListingData.getPrice());
        existingListing.setRating(newListingData.getRating());
        existingListing.setReviewsId(newListingData.getReviewsId());
        existingListing.setImagesUrl(newListingData.getImagesUrl());
        existingListing.setStock(newListingData.getStock());
        existingListing.setSalesCount(newListingData.getSalesCount());
        existingListing.setCategory(newListingData.getCategory());
        existingListing.setProductCondition(newListingData.getProductCondition());

        return listingsRepositoryPort.save(existingListing);
    }

    @Override
    public void delete(ListingsEntity listingsEntity) {
        listingsRepositoryPort.delete(listingsEntity);
    }

    @Override
    public ListingsEntity findById(ObjectId id) {
        return listingsRepositoryPort.findById(id)
                .orElseThrow(() -> new ListingsNotFoundException("Anúncio com ID " + id + " não encontrado."));
    }

    @Override
    public ListingsEntity findBySku(String sku) {
        return listingsRepositoryPort.findBySku(sku)
                .orElseThrow(() -> new ListingsNotFoundException("Sku: " + sku + " não encontrado."));
    }

    @Override
    public List<ListingsEntity> listAll() {
        return listingsRepositoryPort.listAll();
    }

    private void validateListing(ListingsEntity listingsEntity) {
        Set<ConstraintViolation<ListingsEntity>> violations = validator.validate(listingsEntity);
        if (!violations.isEmpty()) {
            throw new BusinessRuleException(violations.iterator().next().getMessage());
        }
    }

    @Override
    public List<ListingsEntity> searchListings(String partialName, ProductCondition productCondition,
            BigDecimal minPrice, BigDecimal maxPrice, SearchOrdering ordering) {

        return listingsRepositoryPort.searchListings(partialName, productCondition, minPrice, maxPrice, ordering);
    }

    @Override
    public PagedResult<ListingsEntity> searchListingsPaginated(String partialName, ProductCondition productCondition,
            BigDecimal minPrice, BigDecimal maxPrice, SearchOrdering ordering, Pagination pagination) {

        return listingsRepositoryPort.searchListingsPaginated(partialName, productCondition, minPrice, maxPrice,
                ordering, pagination);
    }

    @Override
    public List<ListingsEntity> findRelatedBySku(String sku) {
        // 1. Directly query the "recommendations" collection for the given SKU
        Query query = new Query(Criteria.where("sku").is(sku));
        Document recommendationDoc = mongoTemplate.findOne(query, Document.class, "recommendations");

        // 2. If no document is found, return an empty list
        if (recommendationDoc == null) {
            return Collections.emptyList();
        }

        // 3. Extract the list of recommended SKUs from the document
        List<String> recommendedSkus = recommendationDoc.get("recommendations", new ArrayList<String>().getClass());
        if (recommendedSkus.isEmpty()) {
            return Collections.emptyList();
        }

        // 4. Use your existing repository port to fetch the listings
        return listingsRepositoryPort.findAllBySkuIn(recommendedSkus);
    }

    @Override
    public List<ListingsEntity> findAllBySkuIn(List<String> skus) {
    return listingsRepositoryPort.findAllBySkuIn(skus);
    }
}