package com.mercadonosso.listings_service.core.ports.out;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import com.mercadonosso.listings_service.core.domain.ListingsEntity;
import com.mercadonosso.listings_service.core.domain.PagedResult;
import com.mercadonosso.listings_service.core.domain.Pagination;
import com.mercadonosso.listings_service.core.domain.enums.ProductCondition;
import com.mercadonosso.listings_service.core.domain.enums.SearchOrdering;

@Repository
public interface ListingsRepositoryPort {
        ListingsEntity save(ListingsEntity listingsEntity);

        void delete(ListingsEntity listingsEntity);

        List<ListingsEntity> listAll();

        Optional<ListingsEntity> findById(ObjectId id);

        Optional<ListingsEntity> findBySku(String sku);

        List<ListingsEntity> searchListings(String partialName, ProductCondition productCondition,
                        BigDecimal minPrice, BigDecimal maxPrice, SearchOrdering ordering);

        PagedResult<ListingsEntity> searchListingsPaginated(String partialName, ProductCondition productCondition,
                        BigDecimal minPrice, BigDecimal maxPrice, SearchOrdering ordering, Pagination pagination);
}
