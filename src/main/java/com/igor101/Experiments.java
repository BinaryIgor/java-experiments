package com.igor101;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Experiments {
    private static final String VARIABLE_PATTERN_STR = "\\$([a-zA-Z0-9-_.]+)";
    private static final Pattern VARIABLE_PATTERN = Pattern.compile(VARIABLE_PATTERN_STR);
    private static final Random RANDOM = new SecureRandom();

    public static void main(String[] args) throws Exception {
//        var executor = Executors.newVirtualThreadPerTaskExecutor();
//
//        var futures = new ArrayList<Future<Integer>>();
//
//        for (int i = 0; i < 100; i++) {
//            int finalI = i;
//            var f = executor.submit(() -> {
//                try {
//                    Thread.sleep(100);
//                    if (RANDOM.nextBoolean()) {
//                        Thread.yield();
//                    }
//                    Thread.sleep(200);
//                    return 10;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return -1;
//                }
//            });
//
//            futures.add(f);
//        }
//
//        var results = futures.stream()
//                .map(f -> {
//                    try {
//                        return f.get();
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                }}).reduce(Integer::sum);
//
//        System.out.println(results.orElseThrow());

//        var latch = new CountDownLatch(1);
//        var executor = Executors.newVirtualThreadPerTaskExecutor();
//
//        var threads = new CopyOnWriteArrayList<Thread>();
//
//        for (int i =0; i< 100; i++) {
//            executor.submit(() -> {
//                try {
//                    threads.add(Thread.currentThread());
//                    if (latch.await(1, TimeUnit.SECONDS)) {
//                        System.out.println(Instant.now() + ": executing on a thread - " + Thread.currentThread());
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//        }
//
//        Thread.sleep(500);
//
//        latch.countDown();
//        Thread.sleep(500);

        System.out.println(SomeData.class.getClassLoader());
        System.out.println(SomeData.class.getName());
    }

    private static <T> T waitForFutureResult(Future<T> future) {
        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
            this.id = id;
            this.name = name;
        }
    }

    static class SmartData extends SomeData {

        SmartData(long id, String name) {
            super(id, name);
        }
    }
}
