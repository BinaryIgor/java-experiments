package com.igor101.saga.choreography.shipping;

import java.util.UUID;

public record ShippingAccepted(UUID orderId) {
}
