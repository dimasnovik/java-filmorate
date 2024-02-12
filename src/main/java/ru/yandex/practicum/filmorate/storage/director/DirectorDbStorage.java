package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchUserException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Director> directorRowMapper;

    @Override
    public Director add(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("DIRECTORS")
                .usingGeneratedKeyColumns("DIRECTOR_ID");
        Map<String, Object> params = Map.of("DIRECTOR_NAME", director.getName());
        int id = simpleJdbcInsert.executeAndReturnKey(params).intValue();
        director.setId(id);
        return director;
    }

    @Override
    public Director update(Director director) {
        int id = director.getId();
        jdbcTemplate.update("UPDATE directors SET director_name = ? WHERE director_id = ?",
                director.getName(), id);
        return director;
    }

    @Override
    public void remove(int id) {
        jdbcTemplate.update("delete from DIRECTORS where DIRECTOR_ID = ?", id);
    }

    @Override
    public Director getById(int id) {
        return jdbcTemplate.queryForObject("select * from DIRECTORS where DIRECTOR_ID = ?", directorRowMapper, id);
    }

    @Override
    public Collection<Director> getAll() {
        return jdbcTemplate.query("select * from DIRECTORS", directorRowMapper);
    }

    @Override
    public void validateId(int id) {
        try {
            String sqlQuery = "SELECT DIRECTOR_ID FROM DIRECTORS WHERE DIRECTOR_ID = ?;";
            jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
        } catch (DataAccessException e) {
            throw new NoSuchUserException(String.format("Режиссер с id = %d не найден.", id));
        }
    }
}
