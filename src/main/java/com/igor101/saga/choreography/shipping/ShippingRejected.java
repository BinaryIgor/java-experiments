package com.igor101.saga.choreography.shipping;

import java.util.UUID;

public record ShippingRejected(UUID orderId, String message) {
}
