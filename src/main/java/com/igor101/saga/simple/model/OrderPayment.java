package com.igor101.saga.simple.model;

import java.util.UUID;

public record OrderPayment(int amount, PaymentMethod method) {

    public OrderPayment(Payment payment) {
        this(payment.amount(), payment.method());
    }

    public Payment toPayment(UUID id) {
        return new Payment(id, amount, method);
    }
}
