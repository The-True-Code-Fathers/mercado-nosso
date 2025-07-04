package com.mercadonosso.listings_service.adapters.out.persistence.mongo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.mercadonosso.listings_service.core.domain.ListingsEntity;
import com.mercadonosso.listings_service.core.ports.out.ListingsRepositoryPort;

@Component
@Repository
public class ListingsRepositoryAdapter implements ListingsRepositoryPort {
    private final SpringListingsRepository mongoRepository;
    private final ListingsMapper mapper;

    public ListingsRepositoryAdapter(SpringListingsRepository springListingsRepository, ListingsMapper mapper) {
        this.mongoRepository = springListingsRepository;
        this.mapper = mapper;
    }

    @Override
    public ListingsEntity save(ListingsEntity listingsEntity) {
        ListingsModel model = mapper.toModel(listingsEntity);
        ListingsModel savedModel = mongoRepository.save(model);
        return listingsEntity;
    }

    @Override
    public Optional<ListingsEntity> findById(UUID id) {
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
}
