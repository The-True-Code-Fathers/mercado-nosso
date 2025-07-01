package core.ports.in;

import core.domain.Listing;
import java.util.UUID;

public interface ListingServicePort {
    Listing createNewListing(Listing listing);
    Listing searchListingById(UUID id);
    void deleteListing(Listing listing);
}
