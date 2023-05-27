package com.igor101.streaming;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamExtension {

    public static <T extends Timestamped> Map<Instant, Collection<T>> inWindows(Collection<T> collection,
                                                                                ChronoUnit unit,
                                                                                int amount) {
        return Map.of();
    }

    public static <T extends Timestamped> Map<Instant, List<T>> inWindows(Collection<T> collection,
                                                                          ChronoUnit unit) {
        return collection.stream()
                .collect(Collectors.groupingBy(e -> e.timestamp().truncatedTo(unit)));
    }

    public static <K, V extends Timestamped> Map<Instant, Map<K, Collection<V>>> inWindows(Map<K, Collection<V>> map,
                                                                                           ChronoUnit unit,
                                                                                           int amount) {
        return Map.of();
    }
}
