package core.usecases;

import core.domain.Listing;
import core.ports.in.ListingServicePort;
import core.ports.out.ListingRepositoryPort;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Set;
import java.util.UUID;

public class ListingServiceImpl implements ListingServicePort {
    private final ListingRepositoryPort listingRepositoryPort;
    private final Validator validator;

    public ListingServiceImpl(ListingRepositoryPort listingRepositoryPort, Validator validator) {
        this.listingRepositoryPort = listingRepositoryPort;
        this.validator = validator;
    }

    public Listing createNewListing(Listing listing) {
        // Implementar depois
        return listingRepositoryPort.save(listing);
    }

    @Override
    public Listing searchListingById(UUID id) {
        return null;
    }

    @Override
    public void deleteListing(Listing listing) {

    }

    private void validateListing(Listing listing) {
        Set<ConstraintViolation<Listing>> violations = validator.validate(listing);
        if (!violations.isEmpty()) {
        }

    }
}