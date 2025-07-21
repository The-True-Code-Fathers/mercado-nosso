package com.mercadonosso.orders_service.core.usecases;

import com.mercadonosso.orders_service.adapters.in.dto.DashboardResponse;
import com.mercadonosso.orders_service.core.domain.Order;
import com.mercadonosso.orders_service.core.ports.in.DashboardServicePort;
import com.mercadonosso.orders_service.core.ports.in.OrdersServicePort;
import com.mercadonosso.orders_service.core.ports.out.ListingsServicePort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço de aplicação para operações de dashboard
 * Contém a lógica de negócio para geração de métricas de vendas
 */
@Service
public class DashboardService implements DashboardServicePort {
    
    private final OrdersServicePort ordersService;
    private final ListingsServicePort listingsService;
    
    public DashboardService(OrdersServicePort ordersService, ListingsServicePort listingsService) {
        this.ordersService = ordersService;
        this.listingsService = listingsService;
    }
    
    @Override
    public DashboardResponse generateDashboard(UUID sellerId, String period) {
        System.out.println("=== DEBUG DASHBOARD SERVICE ===");
        System.out.println("Seller ID: " + sellerId);
        System.out.println("Period: " + period);
        
        // Busca pedidos do vendedor
        List<Order> orders = ordersService.findBySellerId(sellerId);
        System.out.println("Found orders: " + orders.size());
        
        // Filtra pedidos por período se especificado
        if (period != null) {
            orders = filterOrdersByPeriod(orders, period);
            System.out.println("Orders after period filter: " + orders.size());
        }
        
        // Enriquece pedidos com informações dos listings
        List<DashboardResponse.OrderWithListing> ordersWithListings = enrichOrdersWithListings(orders);
        
        // Calcula métricas
        DashboardMetrics metrics = calculateMetrics(ordersWithListings);
        
        System.out.println("=== DEBUG DASHBOARD RESULTS ===");
        System.out.println("Total Sales: " + metrics.totalSales());
        System.out.println("Average Rating: " + metrics.averageRating());
        System.out.println("Top Products: " + metrics.topProducts().size());
        
        return new DashboardResponse(
                metrics.totalSales(),
                metrics.averageRating(),
                ordersWithListings,
                metrics.topProducts()
        );
    }
    
    private List<Order> filterOrdersByPeriod(List<Order> orders, String period) {
        if (period == null || period.isEmpty()) {
            return orders;
        }
        
        LocalDateTime cutoffDate = calculateCutoffDate(period);
        
        return orders.stream()
                .filter(order -> order.getDate().isAfter(cutoffDate))
                .collect(Collectors.toList());
    }
    
    private LocalDateTime calculateCutoffDate(String period) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (period.toLowerCase()) {
            case "week":
                return now.minusWeeks(1);
            case "month":
                return now.minusMonths(1);
            case "quarter":
                return now.minusMonths(3);
            case "year":
                return now.minusYears(1);
            default:
                return now.minusMonths(1); // default to last month
        }
    }
    
    private List<DashboardResponse.OrderWithListing> enrichOrdersWithListings(List<Order> orders) {
        List<DashboardResponse.OrderWithListing> ordersWithListings = new ArrayList<>();
        
        for (Order order : orders) {
            List<DashboardResponse.ListingInfo> listingInfos = new ArrayList<>();
            
            // Para cada listing no pedido, busca as informações
            for (String listingId : order.getListingIds()) {
                listingsService.findListingById(listingId)
                        .ifPresent(listingInfos::add);
            }
            
            ordersWithListings.add(new DashboardResponse.OrderWithListing(
                    order.getOrderId(),
                    order.getBuyerId(),
                    order.getSellerId(),
                    order.getStatus(),
                    order.getDate(),
                    listingInfos
            ));
        }
        
        return ordersWithListings;
    }
    
    private DashboardMetrics calculateMetrics(List<DashboardResponse.OrderWithListing> ordersWithListings) {
        BigDecimal totalSales = BigDecimal.ZERO;
        double totalRating = 0.0;
        int ratingCount = 0;
        
        Map<String, Integer> productSalesCount = new HashMap<>();
        Map<String, DashboardResponse.TopProduct> topProductsMap = new HashMap<>();
        
        for (DashboardResponse.OrderWithListing orderWithListing : ordersWithListings) {
            for (DashboardResponse.ListingInfo listing : orderWithListing.listings()) {
                // Soma total de vendas
                totalSales = totalSales.add(listing.price());
                
                // Calcula rating médio
                if (listing.rating() != null && listing.rating() > 0) {
                    totalRating += listing.rating();
                    ratingCount++;
                }
                
                // Conta vendas por produto
                String productKey = listing.title();
                productSalesCount.put(productKey, productSalesCount.getOrDefault(productKey, 0) + 1);
                
                // Atualiza top produtos
                DashboardResponse.TopProduct existingProduct = topProductsMap.get(productKey);
                if (existingProduct != null) {
                    BigDecimal newRevenue = existingProduct.totalRevenue().add(listing.price());
                    topProductsMap.put(productKey, new DashboardResponse.TopProduct(
                            productKey,
                            productSalesCount.get(productKey),
                            newRevenue
                    ));
                } else {
                    topProductsMap.put(productKey, new DashboardResponse.TopProduct(
                            productKey,
                            1,
                            listing.price()
                    ));
                }
            }
        }
        
        double averageRating = ratingCount > 0 ? totalRating / ratingCount : 0.0;
        averageRating = Math.round(averageRating * 10.0) / 10.0;
        
        // Ordena produtos por quantidade vendida
        List<DashboardResponse.TopProduct> topProducts = topProductsMap.values()
                .stream()
                .sorted((a, b) -> Integer.compare(b.quantitySold(), a.quantitySold()))
                .collect(Collectors.toList());
        
        return new DashboardMetrics(totalSales, averageRating, topProducts);
    }
    
    /**
     * Record para encapsular as métricas calculadas
     */
    private record DashboardMetrics(
            BigDecimal totalSales,
            Double averageRating,
            List<DashboardResponse.TopProduct> topProducts
    ) {}
}
