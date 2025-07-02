package com.mercadonosso.listings_service.adapters.out.persistence.mongo;

import com.mercadonosso.listings_service.core.domain.ListingsEntity;
import com.mercadonosso.listings_service.core.ports.out.ListingsRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ListingsRepositoryAdapter implements ListingsRepositoryPort {
    private final SpringListingRepository mongoRepository;
    private final ListingMapper mapper;

    public ListingsRepositoryAdapter(SpringListingRepository springListingRepository, ListingMapper mapper) {
        this.mongoRepository = springListingRepository;
        this.mapper = mapper;
    }

    @Override
    public ListingsEntity save(ListingsEntity listingsEntity) {
        ListingModel model = mapper.toModel(listingsEntity);
        ListingModel savedModel = mongoRepository.save(model);
        return listingsEntity;
    }

    @Override
    public Optional<ListingsEntity> searchById(UUID id) {
        Optional<ListingModel> modelOptional = mongoRepository.findById(id.toString());
        return modelOptional.map(mapper::toDomain);
    }

    @Override
    public List<ListingsEntity> listAll() {
        List<ListingModel> models = mongoRepository.findAll();
        return models.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(ListingsEntity listingsEntity) {
        ListingModel model = mapper.toModel(listingsEntity);
        mongoRepository.delete(model);
    }
}
