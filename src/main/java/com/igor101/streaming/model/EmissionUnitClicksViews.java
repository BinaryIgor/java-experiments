package com.igor101.streaming.model;

import java.time.Instant;
import java.util.Collection;

public record EmissionUnitClicksViews(Instant timestamp,
                                      EmissionUnit emissionUnit,
                                      Collection<Click> clicks,
                                      Collection<View> views) {
}
