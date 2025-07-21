package com.mercadonosso.orders_service.core.ports.in;

import com.mercadonosso.orders_service.adapters.in.dto.DashboardResponse;
import java.util.UUID;

/**
 * Port para operações de dashboard do vendedor
 * Define as operações de negócio relacionadas ao dashboard
 */
public interface DashboardServicePort {
    
    /**
     * Gera dados do dashboard para um vendedor
     * 
     * @param sellerId ID do vendedor
     * @param period Período para filtrar os dados (opcional)
     * @return Dados do dashboard com métricas de vendas
     */
    DashboardResponse generateDashboard(UUID sellerId, String period);
}
