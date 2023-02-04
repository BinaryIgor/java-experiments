package com.igor101.saga.choreography.order;

import com.igor101.events.EventPublisher;
import com.igor101.events.Events;
import com.igor101.saga.choreography.payment.CreatePayment;
import com.igor101.saga.choreography.payment.PaymentAccepted;
import com.igor101.saga.choreography.payment.PaymentRejected;
import com.igor101.saga.choreography.shipping.ShippingAccepted;
import com.igor101.saga.choreography.shipping.ShippingRejected;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class OrderService {

    private final Map<UUID, Order> orders = new HashMap<>();
    private final EventPublisher eventPublisher;

    public OrderService(Events events) {
        eventPublisher = events.publisher();

        events.subscribe(CreateOrder.class, e -> {
            orders.put(e.order().id(), e.order());
            eventPublisher.publish(new OrderCreated(e.order()));
        });

        events.subscribe(ShippingAccepted.class, e -> {
            updateOrder(e.orderId(), o -> {
                orders.put(o.id(), o.shippingAccepted());
                eventPublisher.publish(new CreatePayment(o.id(), o.payment()));
            });
        });

        events.subscribe(ShippingRejected.class, e -> {
            updateOrder(e.orderId(), o -> orders.put(o.id(), o.shippingRejected()));
        });

        events.subscribe(PaymentAccepted.class, e -> {
            updateOrder(e.orderId(), o -> orders.put(o.id(), o.paid()));
        });

        events.subscribe(PaymentRejected.class, e -> {
            updateOrder(e.orderId(), o -> orders.put(o.id(), o.paymentRejected()));
        });
    }

    private void updateOrder(UUID id, Consumer<Order> consumer) {
        Optional.ofNullable(orders.get(id)).ifPresent(consumer);
    }

    public Order orderOfId(UUID id) {
        return Optional.ofNullable(orders.get(id)).orElseThrow(() -> new RuntimeException("Order doesn't exist"));
    }
}
