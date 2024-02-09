package ru.yandex.practicum.filmorate.storage.director;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.daoUtils.RowMappers;

import java.util.Collection;
import java.util.Map;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class DirectorDbStorage implements DirectorStorage {
    private JdbcTemplate jdbcTemplate;
    private static final RowMapper<Director> directorRowMapper = RowMappers.directorRowMapper();

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
}
