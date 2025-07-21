package com.mercadonosso.orders_service.adapters.out.mongo;

import java.util.List;
import java.util.stream.Collectors;

import com.mercadonosso.orders_service.core.domain.Order;
import com.mercadonosso.orders_service.core.domain.OrderItem;
import com.mercadonosso.orders_service.core.domain.ShippingAddress;
import com.mercadonosso.orders_service.core.domain.PaymentMethod;
import com.mercadonosso.orders_service.core.domain.OrderSummary;
import com.mercadonosso.orders_service.adapters.out.mongo.valueobjects.ShippingAddressModel;
import com.mercadonosso.orders_service.adapters.out.mongo.valueobjects.PaymentMethodModel;
import com.mercadonosso.orders_service.adapters.out.mongo.valueobjects.OrderSummaryModel;

public class OrderMapper {

    /**
     * Converte Order (Domain) para OrderModel (MongoDB)
     * 
     * @param domain - Entidade de domínio
     * @return OrderModel para persistência no MongoDB
     */
    public static OrderModel toModel(Order domain) {
        OrderModel model = new OrderModel();

        if (domain.getOrderId() != null) {
            model.setOrderId(domain.getOrderId());
        }

        model.setOrderId(domain.getOrderId());
        model.setBuyerId(domain.getBuyerId());
        model.setSellerId(domain.getSellerId());
        model.setStatus(domain.getStatus());
        model.setOrderDate(domain.getDate());
        model.setProductIds(domain.getListingIds());

        // === CONVERSÃO DAS NOVAS INFORMAÇÕES ===
        if (domain.getShippingAddress() != null) {
            model.setShippingAddress(toShippingAddressModel(domain.getShippingAddress()));
        }

        if (domain.getPaymentMethod() != null) {
            model.setPaymentMethod(toPaymentMethodModel(domain.getPaymentMethod()));
        }

        if (domain.getOrderSummary() != null) {
            model.setOrderSummary(toOrderSummaryModel(domain.getOrderSummary()));
        }

        return model;
    }

    /**
     * Converte OrderModel (MongoDB) para Order (Domain)
     * 
     * @param model - Model do MongoDB
     * @return Order (entidade de domínio)
     */
    public static Order toDomain(OrderModel model) {
        Order order = new Order();

        order.setOrderId(model.getOrderId());
        order.setBuyerId(model.getBuyerId());
        order.setSellerId(model.getSellerId());
        order.setStatus(model.getStatus());
        order.setDate(model.getOrderDate());
        
        // Converter lista de String para lista de OrderItem (cada item com quantidade 1 por padrão)
        if (model.getProductIds() != null) {
            List<OrderItem> orderItems = model.getProductIds().stream()
                    .map(listingId -> new OrderItem(listingId, 1))
                    .collect(Collectors.toList());
            order.setOrderItems(orderItems);
        }

        // === CONVERSÃO DAS NOVAS INFORMAÇÕES ===
        if (model.getShippingAddress() != null) {
            order.setShippingAddress(toShippingAddressDomain(model.getShippingAddress()));
        }

        if (model.getPaymentMethod() != null) {
            order.setPaymentMethod(toPaymentMethodDomain(model.getPaymentMethod()));
        }

        if (model.getOrderSummary() != null) {
            order.setOrderSummary(toOrderSummaryDomain(model.getOrderSummary()));
        }

        return order;
    }

    // === MÉTODOS AUXILIARES DE CONVERSÃO ===

    private static ShippingAddressModel toShippingAddressModel(ShippingAddress domain) {
        return new ShippingAddressModel(
                domain.getStreet(),
                domain.getNumber(),
                domain.getComplement(),
                domain.getNeighborhood(),
                domain.getCity(),
                domain.getState(),
                domain.getZipCode(),
                domain.getFullName(),
                domain.getId(),
                domain.isDefault());
    }

    private static ShippingAddress toShippingAddressDomain(ShippingAddressModel model) {
        return new ShippingAddress(
                model.getStreet(),
                model.getNumber(),
                model.getComplement(),
                model.getNeighborhood(),
                model.getCity(),
                model.getState(),
                model.getZipCode(),
                model.getFullName(),
                model.getId(),
                model.isDefault());
    }

    private static PaymentMethodModel toPaymentMethodModel(PaymentMethod domain) {
        return new PaymentMethodModel(
                domain.getId(),
                domain.getType(),
                domain.getCardNumber(),
                domain.getCardholderName(),
                domain.getExpiryDate(),
                domain.getCvv(),
                domain.getInstallments(),
                domain.getPixKey());
    }

    private static PaymentMethod toPaymentMethodDomain(PaymentMethodModel model) {
        return new PaymentMethod(
                model.getId(),
                model.getType(),
                model.getCardNumber(),
                model.getCardholderName(),
                model.getExpiryDate(),
                model.getCvv(),
                model.getInstallments(),
                model.getPixKey());
    }

    private static OrderSummaryModel toOrderSummaryModel(OrderSummary domain) {
        return new OrderSummaryModel(
                domain.getSubtotal(),
                domain.getShippingTotal(),
                domain.getDiscountTotal(),
                domain.getTotal(),
                domain.getItemsCount());
    }

    private static OrderSummary toOrderSummaryDomain(OrderSummaryModel model) {
        return new OrderSummary(
                model.getSubtotal(),
                model.getShippingTotal(),
                model.getDiscountTotal(),
                model.getTotal(),
                model.getItemsCount());
    }
}
