package com.igor101;

import java.io.BufferedInputStream;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Experiments {
    private static final String VARIABLE_PATTERN_STR = "\\$([a-zA-Z0-9-_.]+)";
    private static final Pattern VARIABLE_PATTERN = Pattern.compile(VARIABLE_PATTERN_STR);
    private static final Random RANDOM = new SecureRandom();

    public static void main(String[] args) throws Exception {
        var data1 = new SomeData(1, "Ala");
        var data2 = new SomeData(2, "Bob");
        var data3 = new SomeData(3, "Bob");

        System.out.println(data1);
        System.out.println(data2);
        System.out.println(data3);
        System.out.println(new SomeData(1, "Ala"));
        System.out.println(new SomeData(1, "Ala"));
        System.out.println(new SomeData(1, "Ala"));

        System.out.println("---");
        System.out.println("Map tests...");
        System.out.println("----");

        var map =new HashMap<SomeData, Integer>();
        map.put(data1, 1);
        map.put(data1, 1);
        map.put(data1, 1);
        map.put(new SmartData(1, "Ala"), 1);
        map.put(new SmartData(1, "Ala"), 1);

        System.out.println(map);
    }

    private static String randomId() {
        var bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }


    static class SomeData {
        final long id;
        final String name;

        public SomeData(long id, String name) {
            this.id  = id;
            this.name = name;
        }
    }

    static class SmartData extends SomeData {

        SmartData(long id, String name) {
            super(id, name);
        }
    }
}
