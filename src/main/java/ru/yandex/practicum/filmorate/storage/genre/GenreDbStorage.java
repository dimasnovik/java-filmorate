package ru.yandex.practicum.filmorate.storage.genre;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class GenreDbStorage implements GenreStorage {
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

    public void saveGenresOfFilm(Film film) {
        jdbcTemplate.update("delete from FILMS_GENRES where FILM_ID = ?", film.getId());
        List<Object[]> data = new ArrayList<>();
        Collection<Genre> genres = film.getGenres();
        for (Genre genre : genres) {
            Object[] row = new Integer[]{film.getId(), genre.getId()};
            data.add(row);
        }
        if (!film.getGenres().isEmpty()) {
            jdbcTemplate.batchUpdate("insert into FILMS_GENRES(FILM_ID, GENRE_ID)  values(?,?)", data);
        } else {
            log.info(String.format("у  фильма с id = %d  не указаны жанры", film.getId()));
        }
    }
}
