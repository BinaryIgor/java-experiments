package com.igor101.streaming;

import com.igor101.events.Events;
import com.igor101.streaming.handler.GroupedEmissionUnitClicksViewsBatchHandler;
import com.igor101.streaming.model.Click;
import com.igor101.streaming.model.Emission;
import com.igor101.streaming.model.EmissionUnitClicksViews;
import com.igor101.streaming.model.View;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EmissionClickViewStreamHandler implements EventsStreamHandler {

    private final Map<UUID, Emission> emissions = new ConcurrentHashMap<>();
    private final Map<String, Click> clicks = new ConcurrentHashMap<>();
    private final Map<String, View> views = new ConcurrentHashMap<>();
    private final GroupedEmissionUnitClicksViewsBatchHandler groupedEmissionUnitClicksViewsBatchHandler;

    public EmissionClickViewStreamHandler(
            GroupedEmissionUnitClicksViewsBatchHandler groupedEmissionUnitClicksViewsBatchHandler) {
        this.groupedEmissionUnitClicksViewsBatchHandler = groupedEmissionUnitClicksViewsBatchHandler;

    }

    @Override
    public void subscribe(Events events) {
        events.subscribe(Emission.class, e -> emissions.put(e.id(), e));
        events.subscribe(Click.class, c -> clicks.put(c.emissionUnitId(), c));
        events.subscribe(View.class, v -> views.put(v.emissionUnitId(), v));
    }

    @Override
    public void consume() {
        System.out.printf("Consuming %d emissions, %d clicks and %d views...%n", emissions.size(), clicks.size(),
                views.size());
        System.out.println();

        var windowedEmissions = StreamExtension.inWindows(emissions.values(), ChronoUnit.DAYS);
        var windowedClicks = StreamExtension.inWindows(clicks.values(), ChronoUnit.DAYS);
        var windowedViews = StreamExtension.inWindows(views.values(), ChronoUnit.DAYS);

        var emissionUnitClickViews = groupedEmissionUnitClicksViews(windowedEmissions, windowedClicks, windowedViews);

        groupedEmissionUnitClicksViewsBatchHandler.handle(emissionUnitClickViews);

        //TODO: remove data after timeout!
        removeConsumedEvents(emissionUnitClickViews);

        System.out.printf("Consumed, after cleaning: %d emissions, %d clicks and %d views...%n",
                emissions.size(), clicks.size(), views.size());
        System.out.println();
    }

    private Collection<EmissionUnitClicksViews> groupedEmissionUnitClicksViews(
            Map<Instant, List<Emission>> windowedEmissions,
            Map<Instant, List<Click>> windowedClicks,
            Map<Instant, List<View>> windowedViews) {
        return windowedEmissions.entrySet().stream()
                .flatMap(e -> {
                    var timestamp = e.getKey();
                    var emissions = e.getValue();
                    return singleWindowDataToEmissionUnitClicksViews(emissions,
                            windowedClicks.getOrDefault(timestamp, List.of()),
                            windowedViews.getOrDefault(timestamp, List.of()));
                })
                .toList();
    }


    private Stream<EmissionUnitClicksViews> singleWindowDataToEmissionUnitClicksViews(List<Emission> emissions,
                                                                                      List<Click> clicks,
                                                                                      List<View> views) {
        var clicksByEmissionUnitId = clicks.stream()
                .collect(Collectors.groupingBy(Click::emissionUnitId));
        var viewsByEmissionUnitId = views.stream()
                .collect(Collectors.groupingBy(View::emissionUnitId));

        return emissions.stream()
                .flatMap(e -> {
                    return e.units().stream()
                            .map(eu -> {
                                var emissionUnitId = eu.id();
                                var unitClicks = clicksByEmissionUnitId.getOrDefault(emissionUnitId,
                                        List.of());
                                var unitViews = viewsByEmissionUnitId.getOrDefault(emissionUnitId,
                                        List.of());
                                return new EmissionUnitClicksViews(e.timestamp(), eu, unitClicks,
                                        unitViews);
                            });
                });
    }


    private void removeConsumedEvents(Collection<EmissionUnitClicksViews> consumedEvents) {
        consumedEvents.forEach(e -> {
            e.clicks().forEach(c -> clicks.remove(c.emissionUnitId()));
            e.views().forEach(v -> views.remove(v.emissionUnitId()));
        });
    }
}
