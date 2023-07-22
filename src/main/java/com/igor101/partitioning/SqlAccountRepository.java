package com.igor101.partitioning;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.util.*;

public class SqlAccountRepository {

    private static final Field<UUID> ID_FIELD = DSL.field("id", UUID.class);
    private static final Field<String> NAME_FIELD = DSL.field("name", String.class);
    private static final Field<Integer> COUNTRY_CODE_FIELD = DSL.field("country_code", Integer.class);
    private static final Field<String> DESCRIPTION_FIELD = DSL.field("description", String.class);
    private static final Table<Record> ACCOUNT_TABLE = DSL.table("account");
    private static final Table<Record> ACCOUNT_NOT_PARTITIONED_TABLE = DSL.table("account_not_partitioned");

    private final DSLContext context;

    public SqlAccountRepository(DSLContext context) {
        this.context = context;
    }

    public void preparePartitions(List<Integer> countryCodes) {
        var sql = new StringBuilder();
        countryCodes.forEach(c -> {
            var createPartition = """
                    CREATE TABLE IF NOT EXISTS account_%d
                    PARTITION OF account
                    FOR VALUES IN ('%d');""".formatted(c, c);

            sql.append(createPartition)
                    .append("\n");
        });

        context.execute(sql.toString());
    }

    public void create(List<Account> accounts) {
        var accountInsert = context.insertInto(ACCOUNT_TABLE)
                .columns(ID_FIELD, NAME_FIELD, COUNTRY_CODE_FIELD, DESCRIPTION_FIELD);
        var accountNotPartitionedInsert = context.insertInto(ACCOUNT_NOT_PARTITIONED_TABLE)
                .columns(ID_FIELD, NAME_FIELD, COUNTRY_CODE_FIELD, DESCRIPTION_FIELD);

        accounts.forEach(a -> {
            accountInsert.values(a.id(), a.name(), a.countryCode(), a.description());
            accountNotPartitionedInsert.values(a.id(), a.name(), a.countryCode(), a.description());
        });

        accountInsert.execute();
        accountNotPartitionedInsert.execute();
    }


    public void executeQuery(String query) {
        context.execute(query);
    }
}
