package com.igor101.saga.choreography.payment;

import java.util.UUID;

public record CreatePayment(UUID orderId, int payment) {
}
