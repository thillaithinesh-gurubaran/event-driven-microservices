package com.techbees.product.events.handlers;

import com.techbees.product.entity.ProductLookupEntity;
import com.techbees.product.events.ProductCreatedEvent;
import com.techbees.product.repository.ProductLookupRepository;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Group multiple handlers using processing group, otherwise package will be taken
 * if the event handlers are in different packages, it won't work
 */
@Component
@ProcessingGroup("product-group")
public class ProductsLookupEventsHandler {

    private final ProductLookupRepository productLookupRepository;

    public ProductsLookupEventsHandler(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @EventHandler
    public void on(ProductCreatedEvent event) {
        ProductLookupEntity productLookupEntity =
                new ProductLookupEntity(event.getProductId(), event.getTitle());
        productLookupRepository.save(productLookupEntity);
    }
}
