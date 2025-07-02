package orderservice.core.ports.in;

import orderservice.core.domain.Order;
import orderservice.core.domain.enums.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrdersServicePort {

    Order create(Order order, UUID buyerId, List<UUID> listings);
    Order updateOrder(Order order, OrderStatus status);
    void delete(Order order);
    Order findOrderById(UUID id);
    List<Order> findAllOrders();

}
