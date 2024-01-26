package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.NoSuchElementException;

@Component
@AllArgsConstructor
public class GenreDaoImpl implements GenreDao {
    private JdbcTemplate jdbcTemplate;

    @Override
    public Genre getById(int id) {
        try {
            return jdbcTemplate.queryForObject("select * from GENRES where GENRE_ID = ?", genreRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("genre c id = " + id + " не существует");
        }
    }

    @Override
    public Collection<Genre> getAll() {
        return jdbcTemplate.query("select * from GENRES", genreRowMapper());
    }

    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE_NAME"));
    }
}
