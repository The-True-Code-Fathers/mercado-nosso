package com.mercadonosso.orders_service.adapters.in.dto;

import com.mercadonosso.orders_service.core.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DashboardResponse(
        BigDecimal totalSalesMonth,
        Double averageRating,
        List<OrderWithListing> orders,
        List<TopProduct> topProducts
) {
    public record OrderWithListing(
            UUID orderId,
            UUID buyerId,
            UUID sellerId,
            OrderStatus orderStatus,
            LocalDateTime date,
            List<ListingInfo> listings
    ) {}

    public record ListingInfo(
            String listingId,
            String title,
            BigDecimal price,
            String category,
            Integer rating,
            Integer salesCount
    ) {}

    public record TopProduct(
            String productName,
            int quantitySold,
            BigDecimal totalRevenue
    ) {}
}