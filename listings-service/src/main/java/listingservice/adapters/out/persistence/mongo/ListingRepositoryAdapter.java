package listingservice.adapters.out.persistence.mongo;

import listingservice.core.domain.Listing;
import listingservice.core.ports.out.ListingRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ListingRepositoryAdapter implements ListingRepositoryPort {
    private final SpringListingRepository mongoRepository;
    private final ListingMapper mapper;

    public ListingRepositoryAdapter(SpringListingRepository springListingRepository, ListingMapper mapper) {
        this.mongoRepository = springListingRepository;
        this.mapper = mapper;
    }

    @Override
    public Listing save(Listing listing) {
        ListingModel model = mapper.toModel(listing);
        ListingModel savedModel = mongoRepository.save(model);
        return listing;
    }

    @Override
    public Optional<Listing> searchById(UUID id) {
        Optional<ListingModel> modelOptional = mongoRepository.findById(id.toString());
        return modelOptional.map(mapper::toDomain);
    }

    @Override
    public List<Listing> listAll() {
        List<ListingModel> models = mongoRepository.findAll();
        return models.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Listing listing) {
        ListingModel model = mapper.toModel(listing);
        mongoRepository.delete(model);
    }
}
