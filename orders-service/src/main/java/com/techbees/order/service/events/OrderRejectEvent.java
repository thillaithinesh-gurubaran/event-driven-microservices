package com.techbees.order.service.events;

import com.techbees.order.service.commands.OrderStatus;
import lombok.Value;

@Value
public class OrderRejectEvent {

    private final String orderId;

    private final String reason;

    private final OrderStatus orderStatus = OrderStatus.REJECTED;
}
