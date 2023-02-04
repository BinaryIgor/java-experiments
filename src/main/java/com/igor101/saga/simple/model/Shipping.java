package com.igor101.saga.simple.model;

import java.util.UUID;

public record Shipping(UUID id, String address, ShippingMethod method) {
}
