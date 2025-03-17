package com.techbees.product.events.handlers;

import com.techbees.core.events.ProductReservationCancelledEvent;
import com.techbees.core.events.ProductReservedEvent;
import com.techbees.product.entity.ProductEntity;
import com.techbees.product.events.ProductCreatedEvent;
import com.techbees.product.repository.ProductRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ProcessingGroup("product-group")
public class ProductEventHandler {

    private final ProductRepository productRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductEventHandler.class);


    public ProductEventHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @ExceptionHandler(value = Exception.class)
    public void handle(Exception exception) throws Exception {
        throw exception;
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) {

    }

    @EventHandler
    public void on(ProductCreatedEvent event) {
        ProductEntity productEntity = new ProductEntity();
        // Copy properties
        BeanUtils.copyProperties(event, productEntity);
        // Save
        productRepository.save(productEntity);

        /*if (true) {
            throw new Exception("Forcing exception in the Event Handler class");
        }*/
    }

    @EventHandler
    public void on(ProductReservedEvent productReservedEvent) {
        ProductEntity productEntity = productRepository.findByProductId(productReservedEvent.getProductId());
        LOGGER.debug("ProductReservedEvent - Current Product Quantity : " + productEntity.getQuantity());

        productEntity.setQuantity(productEntity.getQuantity() - productReservedEvent.getQuantity());
        productRepository.save(productEntity);

        LOGGER.debug("ProductReservedEvent - New Product Quantity : " + productEntity.getQuantity());
        LOGGER.info("ProductReservedEvent is called for product id : " + productReservedEvent.getProductId() +
                " and order id : " + productReservedEvent.getOrderId());
    }

    @EventHandler
    public void on(ProductReservationCancelledEvent productReservationCancelledEvent) {
        LOGGER.info("ProductReservedEvent is called for product id : " + productReservationCancelledEvent.getProductId() +
                " and order id : " + productReservationCancelledEvent.getOrderId());

        ProductEntity productEntity = productRepository
                .findByProductId(productReservationCancelledEvent.getProductId());
        LOGGER.debug("ProductReservationCancelledEvent - Current Product Quantity : " + productEntity.getQuantity());

        int newQuantity = productEntity.getQuantity() + productReservationCancelledEvent.getQuantity();
        productEntity.setQuantity(newQuantity);
        // reverse product quantity
        productRepository.save(productEntity);

        LOGGER.debug("ProductReservationCancelledEvent - New Product Quantity : " + productEntity.getQuantity());
    }
}
