package com.igor101.saga.choreography.ads;

import java.util.Set;
import java.util.UUID;

public record Inventory(UUID id, Set<String> brandCodes) {
}
