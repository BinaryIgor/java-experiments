package com.igor101.streaming.repository;

import com.igor101.streaming.model.StatsEntry;

import java.time.Instant;
import java.util.*;

public class InMemoryStatsRepository implements StatsRepository {

    private final Map<Instant, Map<UUID, StatsEntry>> stats = new HashMap<>();

    @Override
    public void upsert(Collection<StatsEntry> entries) {
        synchronized (this) {
            entries.forEach(e -> {
                var timestampEntries = stats.computeIfAbsent(e.timestamp(), k -> new HashMap<>());
                var newEntry = Optional.ofNullable(timestampEntries.get(e.campaignId()))
                        .map(p -> p.add(e))
                        .orElse(e);

                timestampEntries.put(newEntry.campaignId(), newEntry);
            });
        }
    }

    @Override
    public Optional<StatsEntry> ofTimestampAndCampaign(Instant timestamp, UUID campaignId) {
        return Optional.ofNullable(stats.getOrDefault(timestamp, Map.of()).get(campaignId));
    }
}
