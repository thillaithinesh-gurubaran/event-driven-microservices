package com.techbees.order.service.events.handlers;

import com.techbees.order.service.entity.OrderEntity;
import com.techbees.order.service.events.OrderApprovedEvent;
import com.techbees.order.service.events.OrderCreatedEvent;
import com.techbees.order.service.events.OrderRejectEvent;
import com.techbees.order.service.repository.OrderRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ProcessingGroup("order-group")
public class OrderEventHandler {

    private final OrderRepository orderRepository;

    public OrderEventHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @ExceptionHandler(value = Exception.class)
    public void handle(Exception exception) throws Exception {
        throw exception;
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) {

    }

    @EventHandler
    public void on(OrderCreatedEvent event) {
        OrderEntity orderEntity = new OrderEntity();
        // Copy properties
        BeanUtils.copyProperties(event, orderEntity);
        // Save
        orderRepository.save(orderEntity);
    }

    @EventHandler
    public void on(OrderApprovedEvent orderApprovedEvent) {
        OrderEntity orderEntity = orderRepository.findByOrderId(orderApprovedEvent.getOrderId());
        if (orderEntity == null) {
            // TODO
            return;
        }
        orderEntity.setOrderStatus(orderApprovedEvent.getOrderStatus());
        orderRepository.save(orderEntity);
    }

    @EventHandler
    public void on(OrderRejectEvent orderRejectEvent) {
        OrderEntity orderEntity = orderRepository.findByOrderId(orderRejectEvent.getOrderId());
        orderEntity.setOrderStatus(orderRejectEvent.getOrderStatus());
        orderRepository.save(orderEntity);
    }
}
