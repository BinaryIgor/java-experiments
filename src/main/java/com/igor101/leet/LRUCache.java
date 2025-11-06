package com.igor101.leet;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LRUCache {

    private final Map<Integer, Entry> entries;
    private final int capacity;
    private final AtomicInteger nextRank = new AtomicInteger();

    public LRUCache(int capacity) {
        entries = new HashMap<>(capacity);
        this.capacity = capacity;
    }

    public int get(int key) {
        return Optional.ofNullable(entries.get(key))
                .map(e -> {
                    entries.put(key, new Entry(key, e.value, nextRank.getAndIncrement()));
                    return e.value;
                }).orElse(-1);
    }

    public void put(int key, int value) {
        entries.put(key, new Entry(key, value, nextRank.getAndIncrement()));

        if (entries.size() > capacity) {
            var minEntry = entries.values().stream()
                    .min(Comparator.comparing(e -> e.rank))
                    .orElseThrow();

            entries.remove(minEntry.key);
        }
    }

    private record Entry(int key, int value, int rank) {
    }
}
