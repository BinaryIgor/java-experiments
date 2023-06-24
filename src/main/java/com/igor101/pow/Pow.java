package com.igor101.pow;

import java.math.BigInteger;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class Pow {

    private static final Random RANDOM = new Random();
    private static final int PARALLELISM = 10;
    private static final int MAX_SINGLE_CORE_ROUNDS = 100_000;

    public static void tryToSolve(String data, BigInteger difficulty, int maxRounds) {
        System.out.printf("Searching for a pow solution of difficulty %s in max %s rounds...\n", difficulty, maxRounds);

        Optional<BigInteger> nonce;
        if (maxRounds <= MAX_SINGLE_CORE_ROUNDS) {
            nonce = findNonce(data, difficulty, maxRounds);
        } else {
            nonce = findNonceInParallel(data, difficulty, maxRounds);
        }

        System.out.printf("Tried to find a solution in max rounds of %d...\n", maxRounds);
        if (nonce.isPresent()) {
            var nVal = nonce.get();
            System.out.printf("Solution was found and it was a nonce: %d with a hash %s\n",
                    nVal, new BigInteger(Sha.calculate256(data + nVal)));
        } else {
            System.out.println("Solution couldn't be found");
        }
    }

    private static Optional<BigInteger> findNonce(String data, BigInteger difficulty, int maxRounds) {
        for (int i = 0; i < maxRounds; i++) {
            var nonce = nextNonce();
            var sha = new BigInteger(Sha.calculate256(data + nonce));
            if (sha.compareTo(difficulty) < 0) {
                return Optional.of(nonce);
            }
        }

        return Optional.empty();
    }

    private static BigInteger nextNonce() {
        var bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        return new BigInteger(bytes);
    }

    private static Optional<BigInteger> findNonceInParallel(String data, BigInteger difficulty, int maxRounds) {
        var countDownLatch = new CountDownLatch(PARALLELISM);
        var solution = new AtomicReference<BigInteger>();
        var singleMaxRounds = maxRounds / PARALLELISM;

        var executor = Executors.newFixedThreadPool(PARALLELISM);

        for (int i = 0; i < PARALLELISM; i++) {
            executor.execute(() -> {
                findNonce(data, difficulty, singleMaxRounds)
                        .ifPresent(solution::set);
                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await();
            executor.shutdown();
            return Optional.ofNullable(solution.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
