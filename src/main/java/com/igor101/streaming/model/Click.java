package com.igor101.streaming.model;

import com.igor101.streaming.Timestamped;

import java.time.Instant;

public record Click(String emissionUnitId, int effectiveCpc, Instant timestamp) implements Timestamped {
}
