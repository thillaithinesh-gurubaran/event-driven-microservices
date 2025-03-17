package com.techbees.order.service.saga;

import com.techbees.core.FetchUserPaymentDetailsQuery;
import com.techbees.core.commands.CancelProductReservationCommand;
import com.techbees.core.commands.ProcessPaymentCommand;
import com.techbees.core.commands.ReserveProductCommand;
import com.techbees.core.events.PaymentProcessedEvent;
import com.techbees.core.events.ProductReservationCancelledEvent;
import com.techbees.core.events.ProductReservedEvent;
import com.techbees.core.model.User;
import com.techbees.order.service.commands.ApproveOrderCommand;
import com.techbees.order.service.commands.RejectOrderCommand;
import com.techbees.order.service.events.OrderApprovedEvent;
import com.techbees.order.service.events.OrderCreatedEvent;
import com.techbees.order.service.events.OrderRejectEvent;
import com.techbees.order.service.model.OrderSummary;
import com.techbees.order.service.query.FindOrdersQuery;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Saga
public class OrderSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    @Autowired
    private transient QueryUpdateEmitter queryUpdateEmitter;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

    private final String PAYMENT_PROCESSING_TIMEOUT_DEADLINE = "payment-processing-deadline";
    private String scheduleId;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        // Reserve the product
        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .orderId(orderCreatedEvent.getOrderId())
                .productId(orderCreatedEvent.getProductId())
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId())
                .build();

        LOGGER.info("OrderCreatedEvent for order id : " + reserveProductCommand.getOrderId() +
                " and product id : " + reserveProductCommand.getProductId());
        commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {
            @Override
            public void onResult(CommandMessage<? extends ReserveProductCommand> commandMessage,
                                 CommandResultMessage<?> commandResultMessage) {
                if (commandResultMessage.isExceptional()) {
                    // Create and send RejectOrderCommand
                    RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(
                            orderCreatedEvent.getOrderId(),
                            commandResultMessage.exceptionResult().getMessage());
                    // Command gateway
                    commandGateway.send(rejectOrderCommand);
                }
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent) {
        LOGGER.info("ProductReservedEvent for product id : " + productReservedEvent.getProductId() +
                " and order id : " + productReservedEvent.getOrderId());

        FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery =
                new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());

        User userPaymentDetails = null;
        try {
            userPaymentDetails = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
        } catch (Exception exception) {
            LOGGER.error(exception.getMessage());
            cancelProductReservation(productReservedEvent, exception.getMessage());
            return;
        }

        if (userPaymentDetails == null) {
            cancelProductReservation(productReservedEvent, "User payment details not found");
            return;
        }

        LOGGER.info("Successfully fetched user payment details for user : " + userPaymentDetails.getFirstName());

        // Deadline - Trigger an event in certain time period to control the flow
        scheduleId = deadlineManager.schedule(Duration.of(10, ChronoUnit.SECONDS),
                PAYMENT_PROCESSING_TIMEOUT_DEADLINE, userPaymentDetails);

        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(userPaymentDetails.getPaymentDetails())
                .paymentId(UUID.randomUUID().toString())
                .build();

        String result = null;
        try {
            //result = commandGateway.sendAndWait(processPaymentCommand, 10, TimeUnit.SECONDS);
            result = commandGateway.sendAndWait(processPaymentCommand);

        } catch (Exception exception) {
            LOGGER.error(exception.getMessage());
            // TODO Compensation transaction
            cancelProductReservation(productReservedEvent, exception.getMessage());
            return;
        }

        if (result == null) {
            LOGGER.info("Process Payment is NULL, Init compensating transaction(s).");
            // TODO Compensation transaction
            cancelProductReservation(productReservedEvent, "Could not process user payment");
        }
    }

    private void cancelProductReservation(ProductReservedEvent productReservedEvent, String reason) {

        cancelDeadline();

        CancelProductReservationCommand cancelProductReservationCommand =
                CancelProductReservationCommand.builder()
                        .orderId(productReservedEvent.getOrderId())
                        .productId(productReservedEvent.getProductId())
                        .quantity(productReservedEvent.getQuantity())
                        .userId(productReservedEvent.getUserId())
                        .reason(reason)
                        .build();

        commandGateway.send(cancelProductReservationCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {

        cancelDeadline();

        ApproveOrderCommand approveOrderCommand =
                new ApproveOrderCommand(paymentProcessedEvent.getOrderId());

        commandGateway.send(approveOrderCommand);
    }

    private void cancelDeadline() {
        // Deadline - Trigger an event in certain time period to control the flow
        if (scheduleId != null) {
            deadlineManager.cancelSchedule(PAYMENT_PROCESSING_TIMEOUT_DEADLINE, scheduleId);
            scheduleId = null;
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCancelledEvent productReservationCancelledEvent) {
        // Create and send RejectOrderCommand
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(
                productReservationCancelledEvent.getOrderId(),
                productReservationCancelledEvent.getReason());
        // Command gateway
        commandGateway.send(rejectOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApprovedEvent orderApprovedEvent) {
        LOGGER.info("Order is approved, Order Saga is completed for order id : " + orderApprovedEvent.getOrderId());
        //SagaLifecycle.end();
        queryUpdateEmitter.emit(FindOrdersQuery.class, query -> true,
                new OrderSummary(orderApprovedEvent.getOrderId(), "",
                        orderApprovedEvent.getOrderStatus()));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderRejectEvent orderRejectEvent) {
        LOGGER.info("Order is rejected, Order id : " + orderRejectEvent.getOrderId());
        //SagaLifecycle.end();
        queryUpdateEmitter.emit(FindOrdersQuery.class, query -> true,
                new OrderSummary(orderRejectEvent.getOrderId(),
                        orderRejectEvent.getReason(),
                        orderRejectEvent.getOrderStatus()));
    }

    @DeadlineHandler(deadlineName = PAYMENT_PROCESSING_TIMEOUT_DEADLINE)
    public void handlePaymentDeadline(ProductReservedEvent productReservedEvent) {
        LOGGER.info("Payment processing deadline - To cancel product reservation.");
        cancelProductReservation(productReservedEvent, "Payment timeout");
    }
}