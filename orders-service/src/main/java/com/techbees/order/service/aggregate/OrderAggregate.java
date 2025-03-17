package com.techbees.order.service.aggregate;

import com.techbees.order.service.commands.ApproveOrderCommand;
import com.techbees.order.service.commands.CreateOrderCommand;
import com.techbees.order.service.commands.OrderStatus;
import com.techbees.order.service.commands.RejectOrderCommand;
import com.techbees.order.service.events.OrderApprovedEvent;
import com.techbees.order.service.events.OrderCreatedEvent;
import com.techbees.order.service.events.OrderRejectEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;

    private String userId;

    private String productId;

    private Integer quantity;

    private String addressId;

    private OrderStatus orderStatus;
    public OrderAggregate() {

    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand) {
        // Event
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();

        // Copy properties [Based upon the name match]
        BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);

        // Aggregate
        AggregateLifecycle.apply(orderCreatedEvent);
    }

    @CommandHandler
    public void handle(ApproveOrderCommand approveOrderCommand) {
        // TODO Create and Publish the OrderApproveEvent
        OrderApprovedEvent orderApprovedEvent =
                new OrderApprovedEvent(approveOrderCommand.getOrderId(), OrderStatus.APPROVED);

        AggregateLifecycle.apply(orderApprovedEvent);
    }

    @CommandHandler
    public void handle(RejectOrderCommand rejectOrderCommand) {
        OrderRejectEvent orderApprovedEvent = new OrderRejectEvent(
                rejectOrderCommand.getOrderId(), rejectOrderCommand.getReason());

        AggregateLifecycle.apply(orderApprovedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {
        this.orderId = orderCreatedEvent.getOrderId();
        this.userId = orderCreatedEvent.getUserId();
        this.productId = orderCreatedEvent.getProductId();
        this.quantity = orderCreatedEvent.getQuantity();
        this.addressId = orderCreatedEvent.getAddressId();
        this.orderStatus = orderCreatedEvent.getOrderStatus();
    }

    @EventSourcingHandler
    public void on(OrderApprovedEvent orderApprovedEvent) {
        this.orderStatus = orderApprovedEvent.getOrderStatus();
    }

    @EventSourcingHandler
    public void on(OrderRejectEvent orderRejectEvent) {
        this.orderStatus = orderRejectEvent.getOrderStatus();
    }
}
