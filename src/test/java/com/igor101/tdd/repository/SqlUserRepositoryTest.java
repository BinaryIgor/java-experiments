package com.igor101.tdd.repository;

import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.BufferedInputStream;
import java.sql.DriverManager;
import java.util.Optional;

public class SqlUserRepositoryTest {

    private static final String POSTGRES_VERSION = "postgres:14.3";
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(POSTGRES_VERSION);
    private static JdbcTemplate JDBC_TEMPLATE;
    private SqlUserRepository repository;

    @BeforeAll
    static void allSetup() throws Exception {
        POSTGRES.start();
        JDBC_TEMPLATE = jdbcTemplate();
        initSchema();
    }

    private static void initSchema() throws Exception {
        try (var is = new BufferedInputStream(SqlUserRepositoryTest.class.getResourceAsStream("/user.sql"))) {
            var content = is.readAllBytes();
            var sql = new String(content);

            JDBC_TEMPLATE.execute(sql);
        }
    }

    static JdbcTemplate jdbcTemplate() throws Exception {
        var connection = DriverManager.getConnection(POSTGRES.getJdbcUrl(),
                POSTGRES.getUsername(),
                POSTGRES.getPassword());

        return new JdbcTemplate(new SingleConnectionDataSource(connection, true));
    }

    @BeforeEach
    void setup() {
        repository = new SqlUserRepository(JDBC_TEMPLATE);
    }

    @AfterEach
    void tearDown() {
        JDBC_TEMPLATE.execute("truncate \"user\"");
    }

    @AfterAll
    static void allTearDown() {
        POSTGRES.stop();
    }

    @Test
    void ofId_givenExistingUserId_shouldReturnUser() {
        var firstUser = new User(1, "User1", "user1@example.com");
        var secondUser = new User(11, "User11", "user11@example.com");

        createUser(firstUser);
        createUser(secondUser);

        Assertions.assertEquals(repository.ofId(firstUser.id()), Optional.of(firstUser));
        Assertions.assertEquals(repository.ofId(secondUser.id()), Optional.of(secondUser));
    }

    @Test
    void ofId_givenNonExistingUserId_shouldReturnEmpty() {
        var user = new User(2, "Some User", "some.user@email.com");

        createUser(user);

        Assertions.assertEquals(repository.ofId(22), Optional.empty());
    }

    private void createUser(User user) {
        JDBC_TEMPLATE.update("INSERT INTO \"user\" (id, name, email) values (?, ?, ?)",
                user.id(), user.name(), user.email());
    }
}
