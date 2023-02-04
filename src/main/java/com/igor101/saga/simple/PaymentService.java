package com.igor101.saga.simple;

import com.igor101.saga.simple.model.Payment;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PaymentService {

    private final Map<UUID, Payment> payments = new HashMap<>();

    public void save(Payment payment) {
        validatePayment(payment);
        payments.put(payment.id(), payment);
    }

    private void validatePayment(Payment payment) {
        if (payment.amount() < 1) {
            throw new RuntimeException("Payment can't be less than 1");
        }
        if (payment.amount() > 1000) {
            throw new RuntimeException("Payment can't be greater than 1000");
        }
    }

    public Optional<Payment> ofId(UUID id) {
        return Optional.ofNullable(payments.get(id));
    }
}
