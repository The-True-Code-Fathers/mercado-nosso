package core.usecases;

import core.domain.Listing;
import core.domain.exception.BusinessRuleException;
import core.domain.exception.ListingNotFoundException;
import core.ports.in.ListingServicePort;
import core.ports.out.ListingRepositoryPort;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.time.LocalDateTime;
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
        validateListing(listing);
        listing.setCreationDate(LocalDateTime.now());
        listing.setActive(true);
        return listingRepositoryPort.save(listing);
    }

    @Override
    public Listing searchListingById(UUID id) {
        return listingRepositoryPort.searchById(id).orElseThrow(() ->
                new ListingNotFoundException("Anúncio com ID " + id + " não encontrado."));
    }

    @Override
    public void deleteListing(Listing listing) {
        listingRepositoryPort.delete(listing);
    }

    private void validateListing(Listing listing) {
        Set<ConstraintViolation<Listing>> violations = validator.validate(listing);
        if (!violations.isEmpty()) {
            throw new BusinessRuleException(violations.iterator().next().getMessage());
        }
    }
}