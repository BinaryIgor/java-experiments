package com.igor101.streaming.model;

import com.igor101.streaming.Timestamped;

import java.time.Instant;

public record View(String emissionUnitId, Instant timestamp) implements Timestamped {
}
