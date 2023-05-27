package com.igor101.streaming.handler;

import com.igor101.streaming.model.Click;
import com.igor101.streaming.model.EmissionUnitClicksViews;
import com.igor101.streaming.model.StatsEntry;
import com.igor101.streaming.repository.StatsRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.function.Function;

public class GroupedEmissionUnitClicksViewsBatchHandler {

    private final StatsRepository statsRepository;
    private final Function<Instant, Instant> statsEntryTimestampTransformer;

    public GroupedEmissionUnitClicksViewsBatchHandler(StatsRepository statsRepository,
                                                      Function<Instant, Instant> statsEntryTimestampTransformer) {
        this.statsRepository = statsRepository;
        this.statsEntryTimestampTransformer = statsEntryTimestampTransformer;
    }

    public void handle(Collection<EmissionUnitClicksViews> batch) {
        var statsEntries = batch.stream()
                .map(e -> {
                    var cost = e.clicks().stream()
                            .mapToInt(Click::effectiveCpc)
                            .reduce(Integer::sum)
                            .orElse(0);

                    return new StatsEntry(statsEntryTimestampTransformer.apply(e.timestamp()),
                            e.emissionUnit().campaignId(), e.clicks().size(),
                            e.views().size(), cost);
                }).toList();


        statsRepository.upsert(statsEntries);
    }
}
