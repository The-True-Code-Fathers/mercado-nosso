package com.mercadonosso.carts_service.adapters.out.persistence.mongo;

import org.springframework.stereotype.Component;

import com.mercadonosso.carts_service.core.domain.CartsEntity;

@Component
public class CartsMapper {
    public static CartsEntity toDomain(CartsModel model) {
        if (model == null) return null;
        CartsEntity domain = new CartsEntity();
        domain.setId(model.getId());
        domain.setUserId(model.getUserId());
        domain.setItems(model.getItems()); // Reutilização direta
        domain.setSubTotal(model.getSubTotal());
        domain.setShippingPriceTotal(model.getShippingPriceTotal());
        domain.setGrandTotal(model.getGrandTotal());
        domain.setUpdateAt(model.getUpdateAt());
        return domain;
    }

    public static CartsModel toModel(CartsEntity domain) {
        if (domain == null) return null;
        CartsModel model = new CartsModel();
        model.setId(domain.getId());
        model.setUserId(domain.getUserId());
        model.setItems(domain.getItems());
        model.setSubTotal(domain.getSubTotal());
        model.setShippingPriceTotal(domain.getShippingPriceTotal());
        model.setGrandTotal(domain.getGrandTotal());
        model.setUpdateAt(domain.getUpdateAt());
        return model;
    }
}
