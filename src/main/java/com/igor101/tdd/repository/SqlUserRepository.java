package com.igor101.tdd.repository;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

public class SqlUserRepository implements UserRepository {

    private static final String OF_ID_QUERY_TEMPLATE = "SELECT id, name, email FROM \"user\" WHERE id = ?";
    private final JdbcTemplate template;

    public SqlUserRepository(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Optional<User> ofId(long id) {
        var result = template.query(OF_ID_QUERY_TEMPLATE,
                (rs, n) -> new User(rs.getLong("id"), rs.getString("name"),
                        rs.getString("email")),
                id);

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }
}
