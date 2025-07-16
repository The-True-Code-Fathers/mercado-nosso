package com.mercadonosso.reviews_service.adapters.out.http;

import com.mercadonosso.reviews_service.core.ports.out.ListingServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class ListingsServiceAdapter implements ListingServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(ListingsServiceAdapter.class);
    private final RestTemplate restTemplate;
    private final String listingsServiceUrl;

    public ListingsServiceAdapter(RestTemplate restTemplate,
            @Value("${app.services.listings-url}") String listingsServiceUrl) {
        this.restTemplate = restTemplate;
        this.listingsServiceUrl = listingsServiceUrl;
    }

    @Override
    public ListingResponseDTO findListingById(String listingId) { // Changed from UUID to String
        String url = listingsServiceUrl + "/" + listingId;
        logger.info("Tentando buscar listing com ID: {} na URL: {}", listingId, url);

        try {
            ListingResponseDTO response = restTemplate.getForObject(url, ListingResponseDTO.class);
            if (response != null) {
                logger.info("Listing encontrado: {}", response);
            } else {
                logger.warn("Resposta nula para listing ID: {}", listingId);
            }
            return response;
        } catch (HttpClientErrorException.NotFound e) {
            logger.error("Listing não encontrado (404) para ID: {}", listingId);
            return null;
        } catch (RestClientException e) {
            logger.error("Erro ao comunicar com listings-service para ID: {}. Erro: {}", listingId, e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar listing ID: {}. Erro: {}", listingId, e.getMessage(), e);
            return null;
        }
    }
}
