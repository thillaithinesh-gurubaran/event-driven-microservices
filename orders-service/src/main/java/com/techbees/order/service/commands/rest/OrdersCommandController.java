package com.techbees.order.service.commands.rest;

import com.techbees.order.service.commands.CreateOrderCommand;
import com.techbees.order.service.commands.OrderStatus;
import com.techbees.order.service.model.Order;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrdersCommandController {

    private final Environment environment;

    private final CommandGateway commandGateway;

    @Autowired
    public OrdersCommandController(Environment environment, CommandGateway commandGateway) {
        this.environment = environment;
        this.commandGateway = commandGateway;
    }

    @GetMapping("/service/status")
    public String status() {
        return "Order service is up, PORT : " + environment.getProperty("local.server.port");
    }

    @PostMapping
    public String createOrder(@Valid @RequestBody Order order) {
        // Command
        String userId = "27b95829-4f3f-4ddf-8983-151ba010e35b";
        String orderId = UUID.randomUUID().toString();

        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder().addressId(order.getAddressId())
                .productId(order.getProductId()).userId(userId).quantity(order.getQuantity()).orderId(orderId)
                .orderStatus(OrderStatus.CREATED).build();

        String value = commandGateway.sendAndWait(createOrderCommand);
        return value;
    }
}
