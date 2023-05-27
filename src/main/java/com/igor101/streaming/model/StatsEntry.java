package com.igor101.streaming.model;

import java.time.Instant;
import java.util.UUID;

public record StatsEntry(Instant timestamp,
                         UUID campaignId,
                         long clicks,
                         long views,
                         long cost) {
    public StatsEntry add(StatsEntry entry) {
        return add(entry.clicks, entry.views, entry.cost);
    }

    public StatsEntry add(long clicks, long views, long cost) {
        return new StatsEntry(timestamp, campaignId,
                this.clicks + clicks,
                this.views + views,
                this.cost + cost);
    }
}
