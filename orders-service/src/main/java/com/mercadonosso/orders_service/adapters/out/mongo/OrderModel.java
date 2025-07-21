package com.mercadonosso.orders_service.adapters.out.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.mercadonosso.orders_service.core.domain.enums.OrderStatus;
import com.mercadonosso.orders_service.adapters.out.mongo.valueobjects.ShippingAddressModel;
import com.mercadonosso.orders_service.adapters.out.mongo.valueobjects.PaymentMethodModel;
import com.mercadonosso.orders_service.adapters.out.mongo.valueobjects.OrderSummaryModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Document(collection = "orders")
public class OrderModel {

    @Id
    private UUID orderId;

    @Field("buyer_id")
    private UUID buyerId;

    @Field("seller_id")
    private UUID sellerId;

    @Field("order_status")
    private OrderStatus status;

    @Field("order_date")
    private LocalDateTime orderDate;

    @Field("product_ids")
    private List<String> productIds;

    // === NOVAS INFORMAÇÕES DO CHECKOUT ===

    @Field("shipping_address")
    private ShippingAddressModel shippingAddress;

    @Field("payment_method")
    private PaymentMethodModel paymentMethod;

    @Field("order_summary")
    private OrderSummaryModel orderSummary;
}
