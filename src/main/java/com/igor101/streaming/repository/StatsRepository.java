package com.igor101.streaming.repository;

import com.igor101.streaming.model.StatsEntry;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface StatsRepository {
    void upsert(Collection<StatsEntry> entries);

    Optional<StatsEntry> ofTimestampAndCampaign(Instant timestamp, UUID campaignId);
}
