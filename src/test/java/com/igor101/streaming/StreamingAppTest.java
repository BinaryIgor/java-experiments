package com.igor101.streaming;

import com.igor101.events.EventPublisher;
import com.igor101.events.InMemoryEvents;
import com.igor101.streaming.handler.GroupedEmissionUnitClicksViewsBatchHandler;
import com.igor101.streaming.model.StatsEntry;
import com.igor101.streaming.repository.InMemoryStatsRepository;
import com.igor101.streaming.repository.StatsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StreamingAppTest {

    private static final long CONSUME_DELAY_SECONDS = 1;
    private StreamingApp app;
    private EventPublisher publisher;
    private StatsRepository statsRepository;
    private ExecutorService publishingExecutor;

    @BeforeEach
    void setup() {
        var events = new InMemoryEvents();
        publisher = events.publisher();
        statsRepository = new InMemoryStatsRepository();

        var batchHandler = new GroupedEmissionUnitClicksViewsBatchHandler(statsRepository,
                timestamp -> timestamp.truncatedTo(ChronoUnit.DAYS));
        var eventsStreamHandler = new EmissionClickViewStreamHandler(batchHandler);

        app = new StreamingApp(events, eventsStreamHandler, CONSUME_DELAY_SECONDS);

        publishingExecutor = Executors.newFixedThreadPool(10);
    }

    @Test
    void shouldCalculateProperStats() throws Exception {
        app.start();

        var testCase = prepareTestCase();

        testCase.events.forEach(e -> {
            publishOnExecutorWithRandomDelay(e.emission());
            e.clicks().forEach(this::publishOnExecutorWithRandomDelay);
            e.views().forEach(this::publishOnExecutorWithRandomDelay);
        });

        awaitPublishing();
        awaitNextEventsConsumption();

        testCase.expectedStats.forEach(se -> {
            Assertions.assertEquals(Optional.of(se),
                    statsRepository.ofTimestampAndCampaign(se.timestamp(), se.campaignId()));
        });
    }

    private StreamingAppTestCase prepareTestCase() {
        var emissionTimestamps = List.of(
                Instant.parse("2023-05-28T08:00:00Z"),
                Instant.parse("2023-05-28T12:15:00Z"),
                Instant.parse("2023-05-28T17:00:00Z"),
                Instant.parse("2023-05-29T10:15:00Z"),
                Instant.parse("2023-05-29T15:00:00Z"));

        var emissionsWithStats = emissionTimestamps.stream()
                .map(StreamingTester::emissionWithClicksAndViews)
                .toList();

        var eventsToPublish = emissionsWithStats.stream()
                .map(StreamingTester.EmissionWithClicksViewsAndExpectedStats::emission)
                .toList();

        var statsEntriesToCombine = emissionsWithStats.stream()
                .map(StreamingTester.EmissionWithClicksViewsAndExpectedStats::expectedStats)
                .toList();

        var expectedStatsEntries = StreamingTester.combinedStatsEntries(statsEntriesToCombine);

        return new StreamingAppTestCase(eventsToPublish, expectedStatsEntries);
    }

    private <T> void publishOnExecutorWithRandomDelay(T event) {
        publishingExecutor.submit(() -> {
            try {
                Thread.sleep((int) (Math.random() * 1000));
                publisher.publish(event);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void awaitPublishing() throws Exception {
        publishingExecutor.shutdown();
        if (!publishingExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
            throw new RuntimeException("Can't end publishing in 10 seconds!");
        }
    }

    private void awaitNextEventsConsumption() throws Exception {
        Thread.sleep(CONSUME_DELAY_SECONDS * 1500);
    }

    private record StreamingAppTestCase(
            List<StreamingTester.EmissionWithClicksAndViews> events,
            List<StatsEntry> expectedStats
    ) {
    }
}
