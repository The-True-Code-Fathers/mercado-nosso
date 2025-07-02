package com.mercadonosso.listings_service.core.usecases;

import com.mercadonosso.listings_service.core.domain.ListingsEntity;
import com.mercadonosso.listings_service.core.domain.exception.BusinessRuleException;
import com.mercadonosso.listings_service.core.domain.exception.ListingsNotFoundException;
import com.mercadonosso.listings_service.core.ports.in.ListingsServicePort;
import com.mercadonosso.listings_service.core.ports.out.ListingsRepositoryPort;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ListingsServiceImpl implements ListingsServicePort {
    private final ListingsRepositoryPort listingsRepositoryPort;
    private final Validator validator;

    public ListingsServiceImpl(ListingsRepositoryPort listingsRepositoryPort, Validator validator) {
        this.listingsRepositoryPort = listingsRepositoryPort;
        this.validator = validator;
    }

    public ListingsEntity create(ListingsEntity listingsEntity) {
        validateListing(listingsEntity);
        listingsEntity.setCreatedAt(LocalDateTime.now());
        listingsEntity.setActive(true);
        return listingsRepositoryPort.save(listingsEntity);
    }

    @Override
    public ListingsEntity searchById(UUID id) {
        return listingsRepositoryPort.searchById(id).orElseThrow(() ->
                new ListingsNotFoundException("Anúncio com ID " + id + " não encontrado."));
    }

    @Override
    public void delete(ListingsEntity listingsEntity) {
        listingsRepositoryPort.delete(listingsEntity);
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