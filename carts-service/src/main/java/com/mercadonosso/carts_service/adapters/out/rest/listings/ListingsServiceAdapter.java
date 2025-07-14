package com.mercadonosso.carts_service.adapters.out.rest.listings;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.mercadonosso.carts_service.core.ports.out.ListingDetails;
import com.mercadonosso.carts_service.core.ports.out.ListingsServicePort;

@Component
public class ListingsServiceAdapter implements ListingsServicePort {

    private static final Logger logger = LoggerFactory.getLogger(ListingsServiceAdapter.class);
    private final RestTemplate restTemplate;
    private final String listingsServiceUrl;

    public ListingsServiceAdapter(RestTemplate restTemplate,
            @Value("${app.services.listings-url}") String listingsServiceUrl) {
        this.restTemplate = restTemplate;
        this.listingsServiceUrl = listingsServiceUrl;
        logger.info("ListingsServiceAdapter inicializado com URL: {}", listingsServiceUrl);
    }

    @Override
    public Optional<ListingDetails> findListingsById(ObjectId listingId) {
        logger.info("Buscando listing com ID: {} (ObjectId)", listingId);
        logger.info("ObjectId como String: {}", listingId.toHexString());

        try {
            String url = listingsServiceUrl + "/" + listingId.toHexString();
            logger.info("URL completa da requisição: {}", url);

            ListingResponseDto response = restTemplate.getForObject(url, ListingResponseDto.class);
            logger.info("Resposta recebida do listings-service: {}", response);

            if (response != null) {
                logger.info("Convertendo resposta para ListingDetails - listingId: {}, price: {}, stock: {}",
                        response.listingId(), response.price(), response.stock());

                ListingDetails details = new ListingDetails(
                        new ObjectId(response.listingId()),
                        response.price(),
                        response.stock());
                logger.info("ListingDetails criado com sucesso: {}", details);
                return Optional.of(details);
            }
            logger.warn("Resposta nula recebida do listings-service para ID: {}", listingId);
            return Optional.empty();
        } catch (HttpClientErrorException e) {
            logger.error("Erro HTTP ao buscar listing ID {}: Status={}, Body={}",
                    listingId, e.getStatusCode(), e.getResponseBodyAsString(), e);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar listing ID {}", listingId, e);
            return Optional.empty();
        }
    }
}
