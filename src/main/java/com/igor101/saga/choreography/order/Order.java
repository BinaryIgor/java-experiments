package com.igor101.saga.choreography.order;

import com.igor101.saga.choreography.shipping.Shipping;

import java.util.UUID;

public record Order(UUID id, String name, OrderState state, Shipping shipping, int payment) {

    public Order paid() {
        return new Order(id, name, OrderState.PAID, shipping, payment);
    }

    public Order paymentRejected() {
        return new Order(id, name, OrderState.PAYMENT_REJECTED, shipping, payment);
    }

    public Order shippingAccepted() {
        return new Order(id, name, OrderState.SHIPPING_REJECTED, shipping, payment);
    }

    public Order shippingRejected() {
        return new Order(id, name, OrderState.SHIPPING_REJECTED, shipping, payment);
    }

    public static Order newOrder(String name, Shipping shipping, int payment) {
        return new Order(UUID.randomUUID(), name, OrderState.CREATED, shipping, payment);
    }

}
