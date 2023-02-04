package com.igor101.saga.choreography.ads;

import java.util.UUID;

public record InventoryRejected(UUID id, String reason) {
}
