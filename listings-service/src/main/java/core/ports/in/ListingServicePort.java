package core.ports.in;

import core.domain.Listing;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface ListingServicePort {
    Listing create(Listing listing);
    Listing searchById(UUID id);
    void delete(Listing listing);
    List<Listing> listAll();
}
