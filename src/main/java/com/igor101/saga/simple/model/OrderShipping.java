package com.igor101.saga.simple.model;

import java.util.UUID;

public record OrderShipping(String address, ShippingMethod method) {

    public OrderShipping(Shipping shipping) {
        this(shipping.address(), shipping.method());
    }

    public Shipping toShipping(UUID id) {
        return new Shipping(id, address, method);
    }
}
