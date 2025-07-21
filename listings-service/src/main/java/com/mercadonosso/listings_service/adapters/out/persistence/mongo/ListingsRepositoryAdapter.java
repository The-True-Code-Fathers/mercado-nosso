package com.mercadonosso.listings_service.adapters.out.persistence.mongo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
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
            BigDecimal minPrice, BigDecimal maxPrice, SearchOrdering ordering, Pagination pagination) {

        Criteria criteria = buildSearchCriteria(partialName, productCondition, minPrice, maxPrice);

        // Count total elements
        Query countQuery = new Query(criteria);
        long totalElements = mongoTemplate.count(countQuery, ListingsModel.class);

        // Build the main query with pagination and sorting
        Query query = new Query(criteria);

        if (ordering != null) {
            Sort sort = getSort(ordering);
            query.with(sort);
        }

        query.skip(pagination.getOffset()).limit(pagination.getSize());

        List<ListingsModel> models = mongoTemplate.find(query, ListingsModel.class);

        log.info("Search Listings Paginated - Total: {}, Found: {}", totalElements, models.size());

        List<ListingsEntity> entities = models.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());

        return new PagedResult<>(entities, pagination, totalElements);
    }

    private Criteria buildSearchCriteria(String partialName, ProductCondition productCondition,
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

        if (minPrice != null && maxPrice != null) {
            criteria = criteria.and("price").gte(minPrice.doubleValue()).lte(maxPrice.doubleValue());
        } else if (minPrice != null) {
            criteria = criteria.and("price").gte(minPrice.doubleValue());
        } else if (maxPrice != null) {
            criteria = criteria.and("price").lte(maxPrice.doubleValue());
        }

        return criteria;
    }

    private Sort getSort(SearchOrdering ordering) {
        return switch (ordering) {
            case PRICE_ASC -> Sort.by(Sort.Direction.ASC, "price");
            case PRICE_DESC -> Sort.by(Sort.Direction.DESC, "price");
            case NAME_ASC -> Sort.by(Sort.Direction.ASC, "title");
            case NAME_DESC -> Sort.by(Sort.Direction.DESC, "title");
            case CREATED_AT_ASC -> Sort.by(Sort.Direction.ASC, "createdAt");
            case CREATED_AT_DESC -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }
}
