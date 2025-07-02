package listingservice.adapters.out.persistence.mongo;

import listingservice.core.domain.Listing;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class ListingMapper {
    ListingModel model = new ListingModel();

    /**
     *
     * @param domain
     * @return returning validations and builder.
     */
    public ListingModel toModel(Listing domain) {
        if (domain.getListingId() != null) {
            model.setId(domain.getListingId().toString());
        }

        model.setProductId(domain.getProductId());
        model.setSellerId(domain.getSellerId());
        model.setTitle(domain.getTitle());
        model.setDescription(domain.getDescription());
        model.setPrice(domain.getPrice());
        model.setStock(domain.getStock());
        model.setActive(domain.isActive());
        model.setCreationDate(domain.getCreationDate());
        model.setProductCondition(domain.getProductCondition());

        return model;
    }

    /**
     *
     * @param model
     * @return returning as a param model and creating a domain settings attributes
     */
    public Listing toDomain(ListingModel model) {
        Listing domain = new Listing();

        domain.setListingId(UUID.fromString(model.getId()));
        domain.setProductId(model.getProductId());
        domain.setSellerId(model.getSellerId());
        domain.setTitle(model.getTitle());
        domain.setDescription(model.getDescription());
        domain.setPrice(model.getPrice());
        domain.setStock(model.getStock());
        domain.setActive(model.isActive());
        domain.setCreationDate(model.getCreationDate());
        domain.setProductCondition(model.getProductCondition());

        return domain;
    }
}
