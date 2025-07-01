package com.mercadonosso.orders_service.core.ports.out;

import com.mercadonosso.orders_service.core.domain.Orders;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrdersRepositoryPort {

    Orders save(Orders orders);
    Optional<Orders> findById(UUID id);
    List<Orders> findAll();
    void delete(Orders order);
    void deleteById(UUID id);

}
