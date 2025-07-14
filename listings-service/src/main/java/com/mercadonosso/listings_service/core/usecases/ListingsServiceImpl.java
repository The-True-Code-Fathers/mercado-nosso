package com.mercadonosso.listings_service.core.usecases;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.mercadonosso.listings_service.core.domain.ListingsEntity;
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

    public ListingsServiceImpl(ListingsRepositoryPort listingsRepositoryPort, Validator validator) {
        this.listingsRepositoryPort = listingsRepositoryPort;
        this.validator = validator;
    }

    public ListingsEntity create(ListingsEntity listingsEntity) {
        validateListing(listingsEntity);
        listingsEntity.setListingId(new ObjectId());
        listingsEntity.setCreatedAt(LocalDateTime.now());
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
        existingListing.setStock(newListingData.getStock());
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
    public List<ListingsEntity> listAll() {
        return listingsRepositoryPort.listAll();
    }

    private void validateListing(ListingsEntity listingsEntity) {
        Set<ConstraintViolation<ListingsEntity>> violations = validator.validate(listingsEntity);
        if (!violations.isEmpty()) {
            throw new BusinessRuleException(violations.iterator().next().getMessage());
        }
    }
}