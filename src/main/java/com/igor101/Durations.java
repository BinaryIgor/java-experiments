package com.igor101;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.HexFormat;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class Durations {
    public static void main(String[] args) throws Exception {
        System.out.println(Files.probeContentType(Path.of("/home/igor/pictures/Screenshot from 2024-06-19 20-11-25.png")));

        var executor = Executors.newFixedThreadPool(5);

        executor.shutdown();

        try {
            CompletableFuture.runAsync(() -> System.out.println("Should throw an exception"), executor);
        } catch (Exception e) {
            System.out.println("EXception..." + e);
        }
    }
}
