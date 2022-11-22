package com.igor101.sharding;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SqlTodoRepository {

    static final long MAX_ID = 1_000_000;
    private static final TodoMapper TODO_MAPPER = new TodoMapper();
    private final List<JdbcTemplate> shardsTemplates;
    private final ShardResolver shardResolver;

    public SqlTodoRepository(List<JdbcTemplate> shardsTemplates,
                             ShardResolver shardResolver) {
        this.shardsTemplates = shardsTemplates;
        this.shardResolver = shardResolver;
    }

    public static SqlTodoRepository ofHashShards(List<JdbcTemplate> shardsTemplates) {
        var shards = shardsTemplates.size();
        return new SqlTodoRepository(shardsTemplates,
                id -> Objects.hash(id) % shards);
    }

    public static SqlTodoRepository ofRangeShards(List<JdbcTemplate> shardsTemplates) {
        var shards = shardsTemplates.size();
        var shardsMaxValues = new ArrayList<Long>();

        var nextMax = MAX_ID / shards;
        var step = nextMax;
        for (int i = 0; i < shardsTemplates.size(); i++) {
            shardsMaxValues.add(nextMax);
            nextMax += step;
        }
        System.out.println("Shards..." + shardsMaxValues);


        return new SqlTodoRepository(shardsTemplates,
                id -> {
                    int shard = 0;
                    for (var maxShard : shardsMaxValues) {
                        if (maxShard > id) {
                            return shard;
                        }
                        shard++;
                    }

                    return shardsMaxValues.size() - 1;
                });
    }

    public void create(Todo todo) {
        var shard = shardJdbcTemplate(todo.id());

        shard.update("INSERT INTO todo (id, name, description) values (?, ?, ?)",
                todo.id(), todo.name(), todo.description());
    }

    public Optional<Todo> ofId(long id) {
        var result = shardJdbcTemplate(id)
                .query("SELECT * FROM todo WHERE id = ?", TODO_MAPPER, id);

        return result.isEmpty() ? Optional.empty() : Optional.ofNullable(result.get(0));
    }

    public List<Todo> allOfNameLike(String name) {
        return shardsTemplates.parallelStream()
                .map(sht -> sht.query("SELECT * FROM todo WHERE name ilike ?",
                        TODO_MAPPER, "%" + name + "%"))
                .flatMap(Collection::stream)
                .toList();
    }

    private JdbcTemplate shardJdbcTemplate(long id) {
        var shardIdx = shardResolver.shardForId(id);
        System.out.println("Getting %d id, returning %d shard...".formatted(id, shardIdx));
        return shardsTemplates.get(shardIdx);
    }

    public interface ShardResolver {
        int shardForId(long id);
    }

    private static class TodoMapper implements RowMapper<Todo> {

        @Override
        public Todo mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Todo(rs.getLong("id"), rs.getString("name"),
                    rs.getString("description"));
        }
    }
}
