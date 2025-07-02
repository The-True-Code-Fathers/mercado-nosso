package listingservice.core.usecases;

import listingservice.core.domain.Listing;
import listingservice.core.domain.exception.BusinessRuleException;
import listingservice.core.domain.exception.ListingNotFoundException;
import listingservice.core.ports.in.ListingServicePort;
import listingservice.core.ports.out.ListingRepositoryPort;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ListingServiceImpl implements ListingServicePort {
    private final ListingRepositoryPort listingRepositoryPort;
    private final Validator validator;

    public ListingServiceImpl(ListingRepositoryPort listingRepositoryPort, Validator validator) {
        this.listingRepositoryPort = listingRepositoryPort;
        this.validator = validator;
    }

    public Listing create(Listing listing) {
        validateListing(listing);
        listing.setCreationDate(LocalDateTime.now());
        listing.setActive(true);
        return listingRepositoryPort.save(listing);
    }

    @Override
    public Listing searchById(UUID id) {
        return listingRepositoryPort.searchById(id).orElseThrow(() ->
                new ListingNotFoundException("Anúncio com ID " + id + " não encontrado."));
    }

    @Override
    public void delete(Listing listing) {
        listingRepositoryPort.delete(listing);
    }

    @Override
    public List<Listing> listAll() {
        return listingRepositoryPort.listAll();
    }

    private void validateListing(Listing listing) {
        Set<ConstraintViolation<Listing>> violations = validator.validate(listing);
        if (!violations.isEmpty()) {
            throw new BusinessRuleException(violations.iterator().next().getMessage());
        }
    }
}