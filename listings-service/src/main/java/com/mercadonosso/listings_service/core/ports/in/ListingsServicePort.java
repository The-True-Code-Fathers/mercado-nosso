package com.mercadonosso.listings_service.core.ports.in;

import java.math.BigDecimal;
import java.util.List;

import org.bson.types.ObjectId;

import com.mercadonosso.listings_service.core.domain.ListingsEntity;
import com.mercadonosso.listings_service.core.domain.PagedResult;
import com.mercadonosso.listings_service.core.domain.Pagination;
import com.mercadonosso.listings_service.core.domain.enums.ProductCondition;
import com.mercadonosso.listings_service.core.domain.enums.SearchOrdering;

public interface ListingsServicePort {
    ListingsEntity create(ListingsEntity listingsEntity);

    ListingsEntity update(ObjectId id, ListingsEntity listingsEntity);

    void delete(ListingsEntity listingsEntity);

    ListingsEntity findById(ObjectId id);

    List<ListingsEntity> searchListings(String partialName, ProductCondition productCondition, BigDecimal minPrice,
            BigDecimal maxPrice, SearchOrdering ordering);

    PagedResult<ListingsEntity> searchListingsPaginated(String partialName, ProductCondition productCondition, 
            BigDecimal minPrice, BigDecimal maxPrice, SearchOrdering ordering, Pagination pagination);

    List<ListingsEntity> listAll();
}
