package com.techbees.product.errorhandling;

import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.EventMessageHandler;
import org.axonframework.eventhandling.ListenerInvocationErrorHandler;

public class ProductServiceEventsErrorHandler implements ListenerInvocationErrorHandler {
    @Override
    public void onError(Exception exception, EventMessage<?> eventMessage,
                        EventMessageHandler eventMessageHandler) throws Exception {
        throw exception;
    }
}
