package com.mercadonosso.listings_service.adapters.out.persistence.mongo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.mercadonosso.listings_service.core.domain.ListingsEntity;
import com.mercadonosso.listings_service.core.domain.PagedResult;
import com.mercadonosso.listings_service.core.domain.Pagination;
import com.mercadonosso.listings_service.core.domain.enums.ProductCondition;
import com.mercadonosso.listings_service.core.domain.enums.SearchOrdering;
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
    public Optional<ListingsEntity> findBySku(String sku) {
        Optional<ListingsModel> modelOptional = mongoRepository.findBySku(sku);
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
            BigDecimal minPrice, BigDecimal maxPrice, SearchOrdering ordering) {

        Criteria criteria = buildSearchCriteria(partialName, productCondition, minPrice, maxPrice);
        Query query = new Query(criteria);

        // Add sorting based on SearchOrdering
        if (ordering != null) {
            Sort sort = getSort(ordering);
            query.with(sort);
        }

        List<ListingsModel> models = mongoTemplate.find(query, ListingsModel.class);

        return models.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public PagedResult<ListingsEntity> searchListingsPaginated(String partialName, ProductCondition productCondition,
        BigDecimal minPrice, BigDecimal maxPrice, SearchOrdering ordering, Pagination pagination, String category) {

    // 1. Create a list to hold all our filter conditions
    List<Criteria> criteriaList = new ArrayList<>();

    // Always filter for active listings
    criteriaList.add(Criteria.where("active").is(true));

    // Add other filters ONLY if they are provided
    if (partialName != null && !partialName.isEmpty()) {
        criteriaList.add(Criteria.where("title").regex(partialName, "i")); // "i" for case-insensitive
    }
    if (productCondition != null) {
        criteriaList.add(Criteria.where("productCondition").is(productCondition.name()));
    }
    if (minPrice != null) {
        criteriaList.add(Criteria.where("price").gte(minPrice));
    }
    if (maxPrice != null) {
        criteriaList.add(Criteria.where("price").lte(maxPrice));
    }
    // This is the crucial part that was missing from your query
    if (category != null && !category.isEmpty()) {
        criteriaList.add(Criteria.where("category").regex("^" + category + "$", "i")); // Case-insensitive match
    }

    // 2. Build the main query object
    Query query = new Query();
    if (!criteriaList.isEmpty()) {
        query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
    }

    // This is our new log line that will show the CORRECT query
    log.info("Executing MongoDB Query: {}", query.getQueryObject().toJson());

    // 3. Count total results that match the filters
    long totalElements = mongoTemplate.count(query, ListingsModel.class);

    // 4. Apply sorting
    if (ordering != null) {
        Sort sort = getSort(ordering);
        query.with(sort);
    }

    // 5. Apply pagination
    query.skip(pagination.getOffset()).limit(pagination.getSize());

    // 6. Execute the query
    List<ListingsModel> models = mongoTemplate.find(query, ListingsModel.class);
    log.info("Search Listings Paginated - Total: {}, Found: {}", totalElements, models.size());

    // 7. Map results and return
    List<ListingsEntity> entities = models.stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());

    return new PagedResult<>(entities, pagination, totalElements);
}
    private Criteria buildSearchCriteria(String partialName, ProductCondition productCondition,
            BigDecimal minPrice, BigDecimal maxPrice) {
        
    List<Criteria> criteriaList = new ArrayList<>();

    // Always filter for active listings
    criteriaList.add(Criteria.where("active").is(true));

    // 2. Add other conditions to the list if they exist
    if (partialName != null && !partialName.trim().isEmpty()) {
        Criteria textCriteria = new Criteria().orOperator(
                Criteria.where("title").regex(partialName, "i"),
                Criteria.where("description").regex(partialName, "i"));
        criteriaList.add(textCriteria);
    }

    if (productCondition != null) {
        criteriaList.add(Criteria.where("productCondition").is(productCondition.name()));
    }

    if (minPrice != null) {
        criteriaList.add(Criteria.where("price").gte(minPrice));
    }

    if (maxPrice != null) {
        criteriaList.add(Criteria.where("price").lte(maxPrice));
    }

        return new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
    }

    private Sort getSort(SearchOrdering ordering) {
        switch (ordering) {
            case PRICE_ASC:
                return Sort.by(Sort.Direction.ASC, "price");
            case PRICE_DESC:
                return Sort.by(Sort.Direction.DESC, "price");
            case RATING_DESC:
                return Sort.by(Sort.Direction.DESC, "rating");
            case NAME_ASC:
                return Sort.by(Sort.Direction.ASC, "title");
            case NAME_DESC:
                return Sort.by(Sort.Direction.DESC, "title");
            case CREATED_AT_DESC:
                return Sort.by(Sort.Direction.DESC, "createdAt");
            default: // CREATED_AT_ASC
                return Sort.by(Sort.Direction.ASC, "createdAt");
        }
    }

    @Override
    public List<ListingsEntity> findAllBySkuIn(List<String> skus) {
        if (skus == null || skus.isEmpty()) {
            return Collections.emptyList();
        }
        // Find all active products where the SKU is "in" the provided list
        Criteria criteria = Criteria.where("sku").in(skus).and("active").is(true);
        Query query = new Query(criteria);
        List<ListingsModel> models = mongoTemplate.find(query, ListingsModel.class);
        return models.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Document> getCategories() { // 👈 Change the return type
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.group("category").count().as("count"),
            Aggregation.project("count").and("_id").as("key").and("_id").as("name"),
            Aggregation.sort(Sort.Direction.DESC, "count")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation, "listings", Document.class
        );

        // This now works perfectly with no type mismatch.
        return results.getMappedResults();
    }
}
