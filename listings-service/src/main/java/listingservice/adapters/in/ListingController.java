package listingservice.adapters.in;

import listingservice.adapters.in.web.dto.CreatingListingRequest;
import listingservice.adapters.in.web.dto.ListingResponse;
import listingservice.core.domain.Listing;
import listingservice.core.ports.in.ListingServicePort;
import listingservice.core.ports.out.ListingRepositoryPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/listings")
public class ListingController {
    private final ListingServicePort listingServicePort;

    public ListingController(ListingServicePort listingServicePort) {
        this.listingServicePort = listingServicePort;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListingResponse createListing(@RequestBody CreatingListingRequest request) {
        Listing listing = new Listing();
        listing.setProductId(request.productId());
        listing.setSellerId(request.sellerId());
        listing.setTitle(request.title());
        listing.setDescription(request.description());
        listing.setPrice(request.price());
        listing.setStock(request.stock());
        listing.setProductCondition(request.productCondition());
        Listing createdListing = listingServicePort.create(listing);

        return toResponse(createdListing);
    }

    @GetMapping("/{id}")
    public ListingResponse getListingById(@PathVariable UUID id) {
        Listing listing =  listingServicePort.searchById(id);
        return toResponse(listing);
    }

    @GetMapping
    public List<ListingResponse> getAllListings() {
        List<Listing> listings = listingServicePort.listAll();
        return listings.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ListingResponse toResponse(Listing listing) {
        return new ListingResponse(
                listing.getListingId(),
                listing.getTitle(),
                listing.getDescription(),
                listing.getPrice(),
                listing.getStock(),
                listing.isActive(),
                listing.getProductCondition(),
                listing.getCreationDate()
        );
    }
}