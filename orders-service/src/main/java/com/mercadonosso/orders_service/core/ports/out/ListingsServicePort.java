package com.mercadonosso.orders_service.core.ports.out;

import com.mercadonosso.orders_service.adapters.in.dto.DashboardResponse;
import java.util.Optional;

/**
 * Port para integração com o serviço de listings
 * Define operações para buscar informações dos produtos
 */
public interface ListingsServicePort {
    
    /**
     * Busca informações de um listing pelo ID
     * 
     * @param listingId ID do listing
     * @return Informações do listing ou Optional.empty() se não encontrado
     */
    Optional<DashboardResponse.ListingInfo> findListingById(String listingId);
}
