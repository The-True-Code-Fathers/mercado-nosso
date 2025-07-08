package com.mercadonosso.reviews_service.adapters.out.http;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "listings-service", url = "${app.services.listings-url}")
public interface ListingServiceClient {
    @GetMapping("/api/listings/{id}")
    ListingReponseDTO findListingById(@PathVariable("id") UUID id);
}
