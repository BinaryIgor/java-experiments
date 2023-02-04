package com.igor101.saga.simple.model;

import java.util.UUID;

public record Payment(UUID id, int amount, PaymentMethod method) {
}
