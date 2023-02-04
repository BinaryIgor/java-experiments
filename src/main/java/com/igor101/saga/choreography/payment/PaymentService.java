package com.igor101.saga.choreography.payment;

import com.igor101.events.EventPublisher;
import com.igor101.events.Events;

public class PaymentService {

    private final EventPublisher publisher;

    public PaymentService(Events events) {
        publisher = events.publisher();

        events.subscribe(CreatePayment.class, e -> {
            if (isPaymentValid(e.payment())) {
                publisher.publish(new PaymentAccepted(e.orderId()));
            } else {
                publisher.publish(new PaymentRejected(e.orderId(), "Not enough credit!"));
            }

        });

        events.subscribe(DeletePayment.class, e -> {

        });
    }

    private boolean isPaymentValid(int payment) {
        return payment > 0 && payment <= 1000;
    }
}
