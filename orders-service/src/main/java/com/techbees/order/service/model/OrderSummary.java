package com.techbees.order.service.model;

import com.techbees.order.service.commands.OrderStatus;
import lombok.Value;

@Value
public class OrderSummary {

   private final String orderId;

   private final String message;

   private final OrderStatus orderStatus;
}
