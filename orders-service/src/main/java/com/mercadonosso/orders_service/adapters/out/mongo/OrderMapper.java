package com.mercadonosso.orders_service.adapters.out.mongo;

import com.mercadonosso.orders_service.core.domain.Order;

public class OrderMapper {

    /**
     *
     * @param domain
     * @return returning validations and builder.
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
        model.setProductIds(domain.getListingId());

        return model;
    }

    /**
     *
     * @param model
     * @return returning validations and builder.
     */
    public static Order toDomain(OrderModel model) {
        Order order = new Order();

        order.setOrderId(model.getOrderId());
        order.setBuyerId(model.getBuyerId());
        order.setSellerId(model.getSellerId());
        order.setStatus(model.getStatus());
        order.setDate(model.getOrderDate());
        order.setListingId(model.getProductIds());

        return order;
    }
}
