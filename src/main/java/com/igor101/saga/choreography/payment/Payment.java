package com.igor101.saga.choreography.payment;

import java.util.UUID;

public record Payment(UUID orderId, int amount) {
}
