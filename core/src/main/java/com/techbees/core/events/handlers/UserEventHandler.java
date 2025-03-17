package com.techbees.core.events.handlers;

import com.techbees.core.FetchUserPaymentDetailsQuery;
import com.techbees.core.model.PaymentDetails;
import com.techbees.core.model.User;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class UserEventHandler {

    @QueryHandler
    public User findUserPaymentDetails(FetchUserPaymentDetailsQuery query) {

        PaymentDetails paymentDetails = PaymentDetails.builder()
                .cardNumber("5454-card")
                .cvv("123")
                .name("THILLAI THINESH")
                .validUntilMonth(12)
                .validUntilYear(2030)
                .build();

        User user = User.builder()
                .firstName("Thillai Thinesh")
                .lastName("Gurubaran")
                .userId(query.getUserId())
                .paymentDetails(paymentDetails)
                .build();

        return user;
    }
}
