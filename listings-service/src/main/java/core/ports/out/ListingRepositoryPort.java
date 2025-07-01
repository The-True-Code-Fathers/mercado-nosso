package core.ports.out;

import core.domain.Listing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListingRepositoryPort {
    Listing save(Listing listing);
    Optional<Listing> searchById(UUID id);
    List<Listing> listAll();
    void delete(Listing listing);
}
