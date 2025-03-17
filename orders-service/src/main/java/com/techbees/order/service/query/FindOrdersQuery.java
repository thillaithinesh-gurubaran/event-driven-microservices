package com.techbees.order.service.query;

import lombok.Value;

@Value
public class FindOrdersQuery {

    private final String orderId;
}
