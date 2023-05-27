package com.igor101.streaming.model;

import com.igor101.streaming.Timestamped;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Emission(UUID id,
                       Instant timestamp,
                       List<EmissionUnit> units) implements Timestamped {
}
