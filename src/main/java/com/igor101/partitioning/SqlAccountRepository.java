package com.igor101.partitioning;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

public class SqlAccountRepository {

    private final JdbcTemplate template;

    public SqlAccountRepository(JdbcTemplate template) {
        this.template = template;
    }

    public void createPartitions(List<Integer> countryCodes) {
        var sql = new StringBuilder();
        countryCodes.forEach(c -> {
            sql.append("""
                            CREATE TABLE IF NOT EXISTS account_%d
                            PARTITION OF account
                            FOR VALUES IN ('%d');"""
                            .formatted(c, c))
                    .append("\n");
        });

        template.execute(sql.toString());
    }

    public void create(List<Account> accounts) {
        var args = accounts.stream()
                .map(a -> new Object[]{a.id(), a.name(), a.countryCode(), a.description()})
                .toList();

        template.batchUpdate("INSERT INTO account (id, name, country_code, description) VALUES (?, ?, ?, ?)", args);
        template.batchUpdate("INSERT INTO account_not_partitioned (id, name, country_code, description) VALUES (?, ?, ?, ?)",
                args);
    }

}
