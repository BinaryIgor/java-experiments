package com.igor101;

import java.io.File;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExperimentsApp {

    private static final String rootDir = "/tmp/java";
    private static final Random RANDOM = new SecureRandom();

    public static void main(String[] args) throws Exception {
        var container = randomSet(100_000_000);

        System.out.println("Checking for a value...");

        var start = System.nanoTime();

        System.out.println(container.contains(100));

        var duration = Duration.ofNanos(System.nanoTime() - start);

        System.out.println("Checking for a value took: " + duration);

//        var semaphore = new Semaphore(100);
//
//        var container = Executors.newVirtualThreadPerTaskExecutor();
//
//        var start = System.currentTimeMillis();
//
//        var filesToCreate = 1_000_000;
//        for (int i = 0; i < filesToCreate; i++) {
//            var file = "%d.txt".formatted(i);
//            container.execute(() -> {
//                try {
//                    semaphore.acquire();
//                    try (var os = new FileOutputStream(new File(rootDir, file))) {
//                        os.write(Instant.now().toString().getBytes());
//                    }
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                } finally {
//                    semaphore.release();
//                }
//            });
//        }
//
//        container.shutdown();
//
//        if (!container.awaitTermination(5, TimeUnit.MINUTES)) {
//            throw new RuntimeException("Can't finish in 5 minutes!");
//        }
//
//        var duration = System.currentTimeMillis() - start;
//
//        System.out.println("It took: " + Duration.ofMillis(duration));
    }

    private static Set<Integer> randomSet(int size) {
        return Stream.generate(RANDOM::nextInt)
                .limit(size)
                .collect(Collectors.toSet());
    }

    private static List<Integer> randomList(int size) {
        return Stream.generate(RANDOM::nextInt)
                .limit(size)
                .toList();
    }

    private static void executeOnVirtual(Executor executor, Runnable runnable, CountDownLatch latch) {
        executor.execute(() -> {
            Thread.startVirtualThread(() -> {
                try {
                    runnable.run();
                } finally {
                    latch.countDown();
                }
            });
        });
    }
}
