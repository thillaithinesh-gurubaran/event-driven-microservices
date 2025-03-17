package com.techbees.product.commands.interceptor;

import com.techbees.product.commands.CreateProductCommand;
import com.techbees.product.entity.ProductLookupEntity;
import com.techbees.product.repository.ProductLookupRepository;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;

@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private final ProductLookupRepository productLookupRepository;


    private static final Logger LOGGER = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);

    public CreateProductCommandInterceptor(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            List<? extends CommandMessage<?>> list) {
        return (index, command) -> {

            LOGGER.info("Interceptor Command : " + command.getPayloadType());

            if (CreateProductCommand.class.equals((command.getPayloadType()))) {

                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();

                ProductLookupEntity productLookupEntity = productLookupRepository.findByProductIdOrTitle(
                        createProductCommand.getProductId(), createProductCommand.getTitle()
                );

                if (productLookupEntity != null) {
                    throw new IllegalStateException(
                            String.format("Product with product id %s or title %s already exists",
                                    createProductCommand.getProductId(), createProductCommand.getTitle())
                    );
                }

               /* if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RuntimeException("Price cannot be less or equal than Zero");
                }

                if (createProductCommand.getTitle() == null || createProductCommand.getTitle().isBlank()) {
                    throw new RuntimeException("Title cannot be empty");
                }*/
            }

            return command;
        };
    }
}
