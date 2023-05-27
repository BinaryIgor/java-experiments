package com.igor101.streaming.model;

import java.util.UUID;

public record EmissionUnit(String id, UUID campaignId, int effectiveCpc) {
}
