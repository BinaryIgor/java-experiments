package com.igor101.sharding;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.DriverManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ShardingApp {

    private static final Random RANDOM = new Random();
    private static final char[] CHARS = "abcdfghijklmnopqrstwvuxyz0123456789".toCharArray();

    public static void main(String[] args) throws Exception {
        var shards = 3;
        var todos = 100_000;

        var shardsJdbcTemplates = shardsJdbcTemplates(shards);

        var todosToInsert = Stream.generate(ShardingApp::randomTodo)
                .limit(todos)
                .toList();

        var repository = SqlTodoRepository.ofRangeShards(shardsJdbcTemplates);

//        var executor = Executors.newFixedThreadPool(50);
//
//        todosToInsert.forEach(t -> executor.submit(() -> repository.create(t)));
//
//        executor.shutdown();
//
//        if (executor.awaitTermination(30, TimeUnit.SECONDS)) {
//            System.out.println("Data created!");
//        } else {
//            System.out.println("Didn't finish creating data in 30 seconds...");
//        }

        System.out.println(repository.ofId(704775));
        System.out.println(repository.ofId(458952));
        System.out.println(repository.ofId(5242));

        System.out.println(repository.allOfNameLike("az").size());
    }

    private static List<JdbcTemplate> shardsJdbcTemplates(int shards) {
        return IntStream.range(0, shards)
                .mapToObj(i -> {
                    try {
                        var connection = DriverManager.getConnection(
                                "jdbc:postgresql://localhost:555%d/postgres".formatted(i),
                                "postgres", "postgres");

                        return new JdbcTemplate(new SingleConnectionDataSource(connection, true));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    private static Todo randomTodo() {
        var id = 1 + RANDOM.nextLong(SqlTodoRepository.MAX_ID);
        var name = randomString();
        var description = randomString();

        return new Todo(id, name, description);
    }

    private static String randomString() {
        var chars = new char[1 + RANDOM.nextInt(50)];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = CHARS[RANDOM.nextInt(CHARS.length)];
        }
        return new String(chars);
    }
}
