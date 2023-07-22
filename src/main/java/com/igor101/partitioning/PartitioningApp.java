package com.igor101.partitioning;

import com.zaxxer.hikari.HikariDataSource;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

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
    private final static List<Integer> COUNTRY_CODES = IntStream.range(0, 20).boxed().toList();

    public static void main(String[] args) throws Exception {
        prepareData();
//        executeQueries();
    }

    private static void prepareData() throws Exception {
        var records = 1_000_000;
        var batchSize = 1000;
        var batches = records / batchSize;

        var repository = repository();

        System.out.println("Creating partitions for countries..." + COUNTRY_CODES);

        repository.preparePartitions(COUNTRY_CODES);

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

    private static void executeQueries() throws Exception {
        var repository = repository();

        var queriesInstances = 50;

        var delay = 100;

        var executor = Executors.newFixedThreadPool(50);

        while (true) {
            var queries = List.of("SELECT * FROM account order by name limit 100",
                    "SELECT * FROM account where description = '%s".formatted(randomString()),
                    "SELECT * FROM account_not_partitioned where description = '%s".formatted(randomString()));
            for (int i = 0; i < queriesInstances; i++) {
                executor.submit(() -> {
                    queries.forEach(repository::executeQuery);
                });
            }

            Thread.sleep(delay);
        }
    }

    private static SqlAccountRepository repository() {
        var dataSource = new HikariDataSource();

        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5555/postgres");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");
        dataSource.setMinimumIdle(10);
        dataSource.setMaximumPoolSize(25);

        return new SqlAccountRepository(DSL.using(dataSource, SQLDialect.POSTGRES));
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
