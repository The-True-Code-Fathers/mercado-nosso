package listingservice.core.ports.out;

import listingservice.core.domain.Listing;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListingRepositoryPort {
    Listing save(Listing listing);
    Optional<Listing> searchById(UUID id);
    List<Listing> listAll();
    void delete(Listing listing);
}
