package com.igor101.saga.orchestration.ads;

import java.util.Set;
import java.util.UUID;

public record AccountInventory(UUID accountId, Set<String> brandCodes) {
}
