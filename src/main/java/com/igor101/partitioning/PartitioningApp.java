package com.igor101.partitioning;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PartitioningApp {

    private static final Random RANDOM = new Random();
    private static final char[] CHARS = "abcdfghijklmnopqrstwvuxyz0123456789".toCharArray();
    private final static AtomicLong INSERTED_RECORDS = new AtomicLong();
    private final static List<Integer> COUNTRY_CODES = IntStream.range(0, 10).boxed().toList();

    public static void main(String[] args) throws Exception {
        var records = 1_000;
        var batchSize = 1000;
        var batches = records / batchSize;

        var repository = repository();

        System.out.println("Creating partitions for countries..." + COUNTRY_CODES);

        repository.createPartitions(COUNTRY_CODES);

        var executor = Executors.newFixedThreadPool(25);

        var start = System.currentTimeMillis();

        for (int i = 0; i < batches; i++) {
            var toInsert = Stream.generate(PartitioningApp::randomAccount)
                    .limit(batchSize)
                    .toList();

            executor.submit(() -> {
                try {
                    repository.create(toInsert);
                    INSERTED_RECORDS.addAndGet(toInsert.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }

        executor.shutdown();

        while (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
            System.out.printf("Inserted %d/%d records, waiting next 10 seconds to finish...\n",
                    INSERTED_RECORDS.get(), records);
        }

        var duration = Duration.ofMillis(System.currentTimeMillis() - start);

        System.out.println(INSERTED_RECORDS.get() + " records were inserted, it took: " + duration);
    }

    private static SqlAccountRepository repository() {
        var dataSource = new HikariDataSource();

        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5555/postgres?rewriteBatchedStatements=true");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");
        dataSource.setMinimumIdle(10);
        dataSource.setMaximumPoolSize(50);

        return new SqlAccountRepository(new JdbcTemplate(dataSource));
    }

    private static Account randomAccount() {
        var name = randomString();
        var description = randomString();
        var countryCode = COUNTRY_CODES.get(RANDOM.nextInt(COUNTRY_CODES.size()));

        return new Account(UUID.randomUUID(), name, countryCode, description);
    }

    private static String randomString() {
        var chars = new char[1 + RANDOM.nextInt(50)];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = CHARS[RANDOM.nextInt(CHARS.length)];
        }
        return new String(chars);
    }
}
