package com.mercadonosso.listings_service.core.ports.in;

import java.math.BigDecimal;

import java.util.List;
import java.util.Map;

import org.bson.Document;
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

        ListingsEntity findBySku(String sku);

        List<ListingsEntity> searchListings(String partialName, ProductCondition productCondition, BigDecimal minPrice,
                        BigDecimal maxPrice, SearchOrdering ordering);

        PagedResult<ListingsEntity> searchListingsPaginated(String partialName, ProductCondition productCondition,
                        BigDecimal minPrice, BigDecimal maxPrice, SearchOrdering ordering, Pagination pagination, String category);

        List<ListingsEntity> listAll();

        List<ListingsEntity> findRelatedBySku(String sku);

        List<ListingsEntity> findAllBySkuIn(List<String> skus);

        /**
         * Fetches a summary of all product categories.
         * @return A list of maps, where each map represents a category.
        */
        List<Document> getCategories();
}
