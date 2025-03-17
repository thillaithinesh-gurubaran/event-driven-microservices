package com.techbees.order.service.query;

import com.techbees.order.service.entity.OrderEntity;
import com.techbees.order.service.model.OrderSummary;
import com.techbees.order.service.repository.OrderRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class OrderQueriesHandler {

    OrderRepository orderRepository;

    public OrderQueriesHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @QueryHandler
    public OrderSummary findOrder(FindOrdersQuery findOrdersQuery) {
        OrderEntity orderEntity = orderRepository.findByOrderId(findOrdersQuery.getOrderId());
        return new OrderSummary(orderEntity.getOrderId(), "",
                orderEntity.getOrderStatus());
    }
}
