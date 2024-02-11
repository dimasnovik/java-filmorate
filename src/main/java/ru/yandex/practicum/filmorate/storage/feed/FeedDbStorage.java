package ru.yandex.practicum.filmorate.storage.feed;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchUserException;
import ru.yandex.practicum.filmorate.model.feed.EventOperation;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.feed.Feed;

import java.util.List;

@Component
@AllArgsConstructor
public class FeedDbStorage implements FeedStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Feed> getFeed(int userId) {
        String sql = String.format("select exists(select user_id from users where user_id = '%s')", userId);
        boolean exist = jdbcTemplate.queryForObject(sql, Boolean.class);
        if (!exist) {
            throw new NoSuchUserException("Пользователь не найден");
        }
        return jdbcTemplate.query("select * from feed where user_id = ?", feedRowMapper(), userId);
    }

    @Override
    public void createFeed(Feed feed) {
        jdbcTemplate.update("insert into feed (timestamp, user_id, event_type, event_operation, entity_id) values(?,?,?,?,?)",
                feed.getTimestamp(), feed.getUserId(), feed.getEventType().toString(), feed.getOperation().toString(), feed.getEntityId());
    }

    private RowMapper<Feed> feedRowMapper() {
        return (rs, rowNum1) -> new Feed(rs.getInt("event_id"),
                rs.getLong("timestamp"), rs.getInt("user_id"), EventType.valueOf(rs.getString("event_type")),
                EventOperation.valueOf(rs.getString("event_operation")), rs.getInt("entity_id"));
    }
}
