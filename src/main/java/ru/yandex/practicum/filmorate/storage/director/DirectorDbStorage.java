package ru.yandex.practicum.filmorate.storage.director;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class DirectorDbStorage implements DirectorStorage {
    private JdbcTemplate jdbcTemplate;

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
        validateId(id);
        jdbcTemplate.update(
                "update DIRECTORS set DIRECTOR_NAME = ? where DIRECTOR_ID = ?", director.getName(), id);
        return director;
    }


    @Override
    public void remove(int id) {
        validateId(id);
        jdbcTemplate.update("delete from DIRECTORS where DIRECTOR_ID = ?",id);
    }


    @Override
    public Director getById(int id) {
        try {
            return jdbcTemplate.queryForObject("select * from DIRECTORS where DIRECTOR_ID = ?", directorRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("director c id = " + id + " не существует");
        }
    }

    @Override
    public Collection<Director> getAll() {
        return jdbcTemplate.query("select * from DIRECTORS", directorRowMapper());
    }

    private RowMapper<Director> directorRowMapper() {
        return (rs, rowNum) -> new Director(rs.getInt("DIRECTOR_ID"), rs.getString("DIRECTOR_NAME"));
    }

    private void validateId(int id) {
        Integer count = jdbcTemplate.queryForObject("select count(*) from DIRECTORS where DIRECTOR_ID = ?", Integer.class, id);

        //noinspection DataFlowIssue
        if (count == 0) {
            throw new NoSuchElementException("Нет режиссера с id = " + id);
        }
    }
}
