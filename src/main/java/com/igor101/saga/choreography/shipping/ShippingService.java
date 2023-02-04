package com.igor101.saga.choreography.shipping;

import com.igor101.events.EventPublisher;
import com.igor101.events.Events;
import com.igor101.saga.choreography.order.OrderCreated;

import java.util.List;

public class ShippingService {

    public static final List<String> SUPPORTED_CITIES = List.of("New York", "Los Angeles");

    private final EventPublisher publisher;

    public ShippingService(Events events) {
        publisher = events.publisher();

        events.subscribe(OrderCreated.class, e -> {
            var order = e.order();
            if (isShippingValid(order.shipping())) {
                publisher.publish(new ShippingAccepted(order.id()));
            } else {
                publisher.publish(new ShippingRejected(order.id(),
                        "Only %s cities are supported".formatted(SUPPORTED_CITIES)));
            }
        });
    }

    private boolean isShippingValid(Shipping shipping) {
        for (var c : SUPPORTED_CITIES) {
            if (shipping.address().contains(c)) {
                return true;
            }
        }
        return false;
    }
}
