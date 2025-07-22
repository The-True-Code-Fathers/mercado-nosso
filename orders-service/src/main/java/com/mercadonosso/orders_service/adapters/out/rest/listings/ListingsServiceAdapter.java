package com.mercadonosso.orders_service.adapters.out.rest.listings;

import com.mercadonosso.orders_service.adapters.in.dto.DashboardResponse;
import com.mercadonosso.orders_service.core.ports.out.ListingsServicePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * Adaptador para integração com o Listings Service
 * Implementa as operações de busca de informações de produtos
 */
@Component
public class ListingsServiceAdapter implements ListingsServicePort {
    
    private final RestTemplate restTemplate;
    private final String listingsServiceUrl;
    
    public ListingsServiceAdapter(RestTemplate restTemplate,
                                 @Value("${app.services.listings-url:http://listings-service:8084}") String listingsServiceUrl) {
        this.restTemplate = restTemplate;
        this.listingsServiceUrl = listingsServiceUrl;
    }
    
    @Override
    public Optional<DashboardResponse.ListingInfo> findListingById(String listingId) {
        try {
            String url = listingsServiceUrl + "/" + listingId;
            @SuppressWarnings("unchecked")
            Map<String, Object> listingResponse = restTemplate.getForObject(url, Map.class);
            
            if (listingResponse != null) {
                String title = (String) listingResponse.get("title");
                BigDecimal price = new BigDecimal(listingResponse.get("price").toString());
                String category = (String) listingResponse.get("category");
                Integer rating = (Integer) listingResponse.get("rating");
                Integer salesCount = (Integer) listingResponse.get("salesCount");
                
                DashboardResponse.ListingInfo listingInfo = new DashboardResponse.ListingInfo(
                        listingId,
                        title,
                        price,
                        category,
                        rating,
                        salesCount
                );
                
                return Optional.of(listingInfo);
            }
            
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Erro ao buscar listing " + listingId + ": " + e.getMessage());
            return Optional.empty();
        }
    }
}
