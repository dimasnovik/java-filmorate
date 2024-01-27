package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.*;

@Component
@Primary
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    @Override
    public Film add(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");
        Map<String, Object> params = Map.of(
                "FILM_NAME", film.getName(), "RELEASE_DATE", film.getReleaseDate().toString(),
                "DESCRIPTION", film.getDescription(), "DURATION", film.getDuration(),
                "MPA_ID", film.getMpa().getId(), "LIKES_COUNT", 0);
        int id = simpleJdbcInsert.executeAndReturnKey(params).intValue();
        film.setId(id);
        genreStorage.saveGenresOfFilm(film);
        String mpaName = jdbcTemplate.queryForObject("select MPA_NAME from MPA where MPA_ID = ?;",
                (rs, rowNum) -> rs.getString("MPA_NAME"), film.getMpa().getId());
        film.getMpa().setName(mpaName);

        log.info(String.format("Фильм %s с id = %d добавлен: %s", film.getName(), id, film));
        return film;
    }

    @Override
    public Film update(Film film) {
        int id = film.getId();
        validateId(id);
        jdbcTemplate.update("update FILMS set " +
                        "FILM_NAME = ?, RELEASE_DATE = ?, DESCRIPTION = ?, DURATION = ?, MPA_ID = ? where FILM_ID = ?",
                film.getName(), film.getReleaseDate().toString(), film.getDescription(),
                film.getDuration(), film.getMpa().getId(), id);
        genreStorage.saveGenresOfFilm(film);

        String mpaName = jdbcTemplate.queryForObject("select MPA_NAME from MPA where MPA_ID = ?;",
                (rs, rowNum) -> rs.getString("MPA_NAME"), film.getMpa().getId());
        film.getMpa().setName(mpaName);
        log.info(String.format("Фильм %s с id = %d изменен", film.getName(), film.getId()));
        return film;

    }

    @Override
    public Film getById(int id) {
        validateId(id);
        return jdbcTemplate.queryForObject(
                "select f.film_id, film_name, release_date, description, duration, f.mpa_id, MPA_NAME, fg.genre_id, genre_name " +
                        "from films_genres fg " +
                        "right join films f on f.film_id = fg.film_id " +
                        "left join genres g on g.genre_id = fg.genre_id " +
                        "join MPA on f.MPA_ID = MPA.MPA_ID " +
                        "where f.film_id = ?" +
                        "order by f.FILM_ID;", filmsRowMapper(), id).get(0);
    }

    @Override
    public Collection<Film> getAll() {
        try {
            return jdbcTemplate.queryForObject(
                    "select f.film_id, film_name, release_date, description, duration, f.mpa_id, mpa_name, fg.genre_id, genre_name " +
                            "from films_genres fg " +
                            "right join films f on f.film_id = fg.film_id " +
                            "left join genres g on g.genre_id = fg.genre_id " +
                            "join MPA on f.MPA_ID = MPA.MPA_ID " +
                            "order by f.FILM_ID;", filmsRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void addLike(int filmId, int userId) {
        jdbcTemplate.update("insert into FILMS_LIKES(FILM_ID, USER_ID) VALUES (?,?)", filmId, userId);
        jdbcTemplate.update("update FILMS set LIKES_COUNT = LIKES_COUNT + 1 where FILM_ID = ?", filmId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        jdbcTemplate.update("delete from FILMS_LIKES where FILM_ID = ? and USER_ID = ?", filmId, userId);
    }

    @Override
    public Collection<Film> getPopular(int count) {
        try {
            return jdbcTemplate.queryForObject(
                    "select f.film_id, film_name, release_date, description, duration, f.mpa_id, mpa_name, fg.genre_id, genre_name " +
                            "from films_genres fg " +
                            "right join films f on f.film_id = fg.film_id " +
                            "left join genres g on g.genre_id = fg.genre_id " +
                            "join MPA on f.MPA_ID = MPA.MPA_ID " +
                            "left join FILMS_LIKES fl on f.film_id = fl.film_id " +
                            "order by f.LIKES_COUNT desc,f.FILM_ID " +
                            "limit ?;", filmsRowMapper(), count);
        } catch (EmptyResultDataAccessException e) {
            log.info(e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Collection<Integer> getLikes(int id) {
        return jdbcTemplate.query("select USER_ID from FILMS_LIKES where FILM_ID = ?",
                ((rs, rowNum) -> rs.getInt("USER_ID")), id);
    }

    private RowMapper<List<Film>> filmsRowMapper() {
        return (rs, rowNum) -> {
            List<Film> films = new ArrayList<>();
            while (!rs.isAfterLast()) {
                int filmId = rs.getInt("FILM_ID");
                Film film = new Film(rs.getString("FILM_NAME"), rs.getString("DESCRIPTION"),
                        rs.getDate("RELEASE_DATE").toLocalDate(), rs.getInt("DURATION"),
                        new Mpa(rs.getInt("MPA_ID"), rs.getString("MPA_NAME")));
                film.setId(filmId);

                do {
                    if (rs.getString("GENRE_NAME") != null) {
                        Genre genre = new Genre(rs.getInt("GENRE_ID"), rs.getString("GENRE_NAME"));
                        film.getGenres().add(genre);
                    }

                } while (rs.next() && filmId == rs.getInt("FILM_ID"));

                films.add(film);
            }
            return films;
        };
    }

    private void validateId(int id) {
        Integer count = jdbcTemplate.queryForObject("select count(*) from FILMS where FILM_ID = ?", Integer.class, id);
        if (count == 0) {
            throw new NoSuchFilmException("Нет фильма с id = " + id);
        }
    }
}
