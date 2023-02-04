package com.igor101.saga.orchestration.ads;

import java.util.UUID;

public record AccountInventoryRejected(UUID accountId, String reason) {
}
