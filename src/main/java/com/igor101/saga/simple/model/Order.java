package com.igor101.saga.simple.model;

import java.util.UUID;

public record Order(UUID id, String name, OrderShipping shipping, OrderPayment payment) {

    public Order withShipping(OrderShipping shipping) {
        return new Order(id, name, shipping, payment);
    }
}
