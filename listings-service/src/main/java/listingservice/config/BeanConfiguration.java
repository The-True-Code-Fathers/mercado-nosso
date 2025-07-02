package listingservice.config;

import listingservice.adapters.out.persistence.mongo.ListingRepositoryAdapter;

import listingservice.core.ports.in.ListingServicePort;
import listingservice.core.usecases.ListingServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.validation.Validator;

@Configuration
public class BeanConfiguration {

    @Bean
    public ListingServicePort listingServicePort(
            ListingRepositoryAdapter listingRepositoryAdapter,
            Validator validator
    ) {
        return new ListingServiceImpl(listingRepositoryAdapter, validator);
    }
}
