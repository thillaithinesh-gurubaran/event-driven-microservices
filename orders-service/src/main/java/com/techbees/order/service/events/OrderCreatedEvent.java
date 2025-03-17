package com.techbees.order.service.events;

import com.techbees.order.service.commands.OrderStatus;
import lombok.Data;

@Data
public class OrderCreatedEvent {

    public String orderId;

    private String userId;

    private String productId;

    private Integer quantity;

    private String addressId;

    private OrderStatus orderStatus;
}
