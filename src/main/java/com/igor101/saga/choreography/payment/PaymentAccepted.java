package com.igor101.saga.choreography.payment;

import java.util.UUID;

public record PaymentAccepted(UUID orderId) {
}
