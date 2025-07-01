package adapters.out.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringListingRepository extends MongoRepository<ListingModel, String> {
}
