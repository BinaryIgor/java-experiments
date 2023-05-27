package com.igor101.streaming;

import com.igor101.streaming.model.*;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class StreamingTester {

    private static final List<UUID> CAMPAIGN_IDS = Stream.generate(UUID::randomUUID)
            .limit(10)
            .toList();
    private static final Random RANDOM = new SecureRandom();

    private static <T> List<T> randomSublist(List<T> list) {
        if (list.size() <= 1) {
            return list;
        }
        var minIdx = randomInt(0, list.size() / 2);
        var maxIdx = randomInt(minIdx, list.size());
        return list.subList(minIdx, maxIdx);
    }

    public static Instant nextEmissionTimestamp(Instant timestamp) {
        var secondsForward = randomInt(0, (int) TimeUnit.DAYS.toSeconds(1));
        return timestamp.plusSeconds(secondsForward);
    }

    private static List<View> newViews(Instant emissionTimestamp,
                                       List<EmissionUnit> emissionUnits) {
        return randomSublist(emissionUnits).stream()
                .map(eu -> new View(eu.id(), nextEmissionTimestamp(emissionTimestamp)))
                .toList();
    }

    private static int randomInt(int min, int max) {
        return min + RANDOM.nextInt(max - min);
    }

    public static EmissionWithClicksViewsAndExpectedStats emissionWithClicksAndViews(Instant emissionTimestamp) {
        var emissionId = UUID.randomUUID();
        var campaignIds = uniqueCampaignIds(4);

        var emissionUnit1 = new EmissionUnit(emissionUnitId(emissionId, 1), campaignIds.get(0), 10);
        var emissionUnit2 = new EmissionUnit(emissionUnitId(emissionId, 2), campaignIds.get(1), 50);
        var emissionUnit3 = new EmissionUnit(emissionUnitId(emissionId, 3), campaignIds.get(2), 100);
        var emissionUnit4 = new EmissionUnit(emissionUnitId(emissionId, 4), campaignIds.get(3), 25);

        var emission = new Emission(emissionId, emissionTimestamp,
                List.of(emissionUnit1, emissionUnit2, emissionUnit3, emissionUnit4));

        var clicks = List.of(
                emissionUnitToClick(emissionUnit1, emissionTimestamp.plusSeconds(1)),
                emissionUnitToClick(emissionUnit2, emissionTimestamp.plusSeconds(100)),
                emissionUnitToClick(emissionUnit4, emissionTimestamp.plus(Duration.ofMinutes(122)))
        );
        var views = List.of(
                emissionUnitToView(emissionUnit1, emissionTimestamp.plus(Duration.ofMinutes(22))),
                emissionUnitToView(emissionUnit2, emissionTimestamp.plusSeconds(10)),
                emissionUnitToView(emissionUnit3, emissionTimestamp.plusSeconds(120)),
                emissionUnitToView(emissionUnit4, emissionTimestamp.plusSeconds(1199))
        );

        var statsTimestamp = statsTruncatedTimestamp(emissionTimestamp);

        var expectedStats = List.of(
                new StatsEntry(statsTimestamp, emissionUnit1.campaignId(), 1, 1,
                        emissionUnit1.effectiveCpc()),
                new StatsEntry(statsTimestamp, emissionUnit2.campaignId(), 1, 1,
                        emissionUnit2.effectiveCpc()),
                new StatsEntry(statsTimestamp, emissionUnit3.campaignId(), 0, 1, 0),
                new StatsEntry(statsTimestamp, emissionUnit4.campaignId(), 1, 1,
                        emissionUnit4.effectiveCpc())
        );

        return new EmissionWithClicksViewsAndExpectedStats(
                new EmissionWithClicksAndViews(emission, clicks, views),
                expectedStats);
    }


    private static List<UUID> uniqueCampaignIds(int size) {
        if (size >= CAMPAIGN_IDS.size()) {
            return CAMPAIGN_IDS;
        }
        var campaignIds = new HashSet<UUID>();
        while (campaignIds.size() != size) {
            campaignIds.add(nextCampaignId());
        }
        return new ArrayList<>(campaignIds);
    }

    private static UUID nextCampaignId() {
        var idx = RANDOM.nextInt(CAMPAIGN_IDS.size());
        return CAMPAIGN_IDS.get(idx);
    }

    private static String emissionUnitId(UUID emissionId, int index) {
        return "%s-%d".formatted(emissionId, index);
    }

    private static Instant statsTruncatedTimestamp(Instant timestamp) {
        return timestamp.truncatedTo(ChronoUnit.DAYS);
    }

    private static Click emissionUnitToClick(EmissionUnit emissionUnit, Instant timestamp) {
        return new Click(emissionUnit.id(), emissionUnit.effectiveCpc(), timestamp);
    }

    private static View emissionUnitToView(EmissionUnit emissionUnit, Instant timestamp) {
        return new View(emissionUnit.id(), timestamp);
    }

    public static List<StatsEntry> combinedStatsEntries(List<List<StatsEntry>> entries) {
        var uniqueStatsEntries = new HashMap<String, StatsEntry>();

        for (var ses : entries) {
            ses.forEach(se -> {
                var key = statsEntryKey(se);

                var newEntry = Optional.ofNullable(uniqueStatsEntries.get(key))
                        .map(p -> p.add(se))
                        .orElse(se);

                uniqueStatsEntries.put(key, newEntry);
            });
        }

        return new ArrayList<>(uniqueStatsEntries.values());
    }

    private static String statsEntryKey(StatsEntry entry) {
        return entry.timestamp() + "-" + entry.campaignId();
    }

    public record EmissionWithClicksAndViews(Emission emission,
                                             List<Click> clicks,
                                             List<View> views) {
    }

    public record EmissionWithClicksViewsAndExpectedStats(EmissionWithClicksAndViews emission,
                                                          List<StatsEntry> expectedStats) {
    }
}
