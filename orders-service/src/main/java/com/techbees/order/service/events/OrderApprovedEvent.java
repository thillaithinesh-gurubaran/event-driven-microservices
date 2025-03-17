package com.techbees.order.service.events;


import com.techbees.order.service.commands.OrderStatus;
import lombok.Value;

@Value
public class OrderApprovedEvent {

    private final String orderId;

    private final OrderStatus orderStatus;
}
