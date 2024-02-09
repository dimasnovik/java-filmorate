package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.daoUtils.RowMappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Primary
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private JdbcTemplate jdbcTemplate;
    private static final RowMapper<List<Film>> filmsRowMapper = RowMappers.filmsRowMapper();

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
        String mpaName = jdbcTemplate.queryForObject("select MPA_NAME from MPA where MPA_ID = ?;",
                (rs, rowNum) -> rs.getString("MPA_NAME"), film.getMpa().getId());
        film.getMpa().setName(mpaName);
        addDirectorToFilm(film);
        log.info(String.format("Фильм %s с id = %d добавлен: %s", film.getName(), id, film));
        return film;
    }

    @Override
    public Film update(Film film) {
        int id = film.getId();
        jdbcTemplate.update("update FILMS set " +
                        "FILM_NAME = ?, RELEASE_DATE = ?, DESCRIPTION = ?, DURATION = ?, MPA_ID = ? where FILM_ID = ?",
                film.getName(), film.getReleaseDate().toString(), film.getDescription(),
                film.getDuration(), film.getMpa().getId(), id);

        String mpaName = jdbcTemplate.queryForObject("select MPA_NAME from MPA where MPA_ID = ?;",
                (rs, rowNum) -> rs.getString("MPA_NAME"), film.getMpa().getId());
        film.getMpa().setName(mpaName);
        addDirectorToFilm(film);
        log.info(String.format("Фильм %s с id = %d изменен", film.getName(), film.getId()));
        return film;

    }

    @Override
    public void deleteById(int id) {
        log.info(String.format("Удаляется фильм с id = %d", id));
        jdbcTemplate.update("delete from FILMS where FILM_ID = ?", id);
        log.info(String.format("Фильм с id = %d успешно удален", id));

    }

    @Override
    public Film getById(int id) {
        return jdbcTemplate.queryForObject(
                "select f.film_id, film_name, release_date, description," +
                        " duration, f.mpa_id, mpa_name, d.DIRECTOR_ID as DIRECTOR_ID, d.DIRECTOR_NAME as DIRECTOR_NAME," +
                        " fg.genre_id, genre_name " +
                        "from films_genres fg " +
                        "right join films f on f.film_id = fg.film_id " +
                        "left join genres g on g.genre_id = fg.genre_id " +
                        "left join DIRECTORS d on f.DIRECTOR_ID = d.DIRECTOR_ID " +
                        "join MPA on f.MPA_ID = MPA.MPA_ID " +
                        "where f.film_id = ? " +
                        "order by f.FILM_ID;", filmsRowMapper, id).get(0);
    }

    @Override
    public Collection<Film> getAll() {
        try {
            return jdbcTemplate.queryForObject(
                    "select f.film_id, film_name, release_date, description," +
                            " duration, f.mpa_id, mpa_name, d.DIRECTOR_ID as DIRECTOR_ID, d.DIRECTOR_NAME as DIRECTOR_NAME," +
                            " fg.genre_id, genre_name " +
                            "from films_genres fg " +
                            "right join films f on f.film_id = fg.film_id " +
                            "left join genres g on g.genre_id = fg.genre_id " +
                            "left join DIRECTORS d on f.DIRECTOR_ID = d.DIRECTOR_ID " +
                            "join MPA on f.MPA_ID = MPA.MPA_ID " +
                            "order by f.FILM_ID;", filmsRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void addLike(int filmId, int userId) {
        try {
            jdbcTemplate.update("INSERT into FILMS_LIKES(FILM_ID, USER_ID) VALUES (?,?);", filmId, userId);
            jdbcTemplate.update("update FILMS set LIKES_COUNT = LIKES_COUNT + 1 where FILM_ID = ?;", filmId);
        } catch (DataAccessException e) {
            log.warn("user already put like to this film");
        }
    }

    @Override
    public void removeLike(int filmId, int userId) {
        int rowsAffected = jdbcTemplate.update("delete from FILMS_LIKES where FILM_ID = ? and USER_ID = ?", filmId, userId);
        if (rowsAffected == 1) {
            jdbcTemplate.update("update FILMS set LIKES_COUNT = LIKES_COUNT - 1 where FILM_ID = ?;", filmId);
        }
    }

    @Override
    public Collection<Film> getPopular(int count, Integer genreId, Integer year) {
        try {
            if (genreId != null && year != null) {
                return jdbcTemplate.queryForObject(
                        "select f.*, m.*, d.*, g.* from films f left join mpa m on f.mpa_id = m.mpa_id " +
                                "left join directors d on f.director_id = d.director_id left join films_genres fg on f.film_id = fg.film_id " +
                                "left join genres g on fg.genre_id = g.genre_id where f.film_id in " +
                                "(select f.film_id from films f left join films_genres fg on f.film_id = fg.film_id " +
                                "left join genres g on fg.genre_id = g.genre_id where g.genre_id = ?) and extract(year from f.release_date) = ? " +
                                "order by f.likes_count desc;", filmsRowMapper, genreId, year).stream().limit(count).collect(Collectors.toList());
            }
            if (genreId != null) {
                return jdbcTemplate.queryForObject(
                        "select f.*, m.*, d.*, g.* from films f left join mpa m on f.mpa_id = m.mpa_id " +
                                "left join directors d on f.director_id = d.director_id left join films_genres fg on f.film_id = fg.film_id " +
                                "left join genres g on fg.genre_id = g.genre_id where f.film_id in " +
                                "(select f.film_id from films f left join films_genres fg on f.film_id = fg.film_id " +
                                "left join genres g on fg.genre_id = g.genre_id where g.genre_id = ?) " +
                                "order by f.likes_count desc;", filmsRowMapper, genreId).stream().limit(count).collect(Collectors.toList());
            }
            if (year != null) {
                return jdbcTemplate.queryForObject(
                        "select f.*, m.*, d.*, fg.*, g.* from films f left join mpa m on f.mpa_id = m.mpa_id " +
                                "left join directors d on f.director_id = d.director_id left join films_genres fg on f.film_id = fg.film_id " +
                                "left join genres g on fg.genre_id = g.genre_id where extract(year from f.release_date) = ? " +
                                "order by f.likes_count desc;", filmsRowMapper, year).stream().limit(count).collect(Collectors.toList());
            }
            return jdbcTemplate.queryForObject(
                    "select f.*, m.*, d.*, g.* from films f left join mpa m on f.mpa_id = m.mpa_id " +
                            "left join directors d on f.director_id = d.director_id left join films_genres fg on f.film_id = fg.film_id " +
                            "left join genres g on fg.genre_id = g.genre_id " +
                            "order by f.likes_count desc;", filmsRowMapper).stream().limit(count).collect(Collectors.toList());

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

    @Override
    public Collection<Film> getCommonPopularFilms(int userId, int friendId, int count) {
        try {
            return jdbcTemplate.queryForObject(

                    "select f.film_id, film_name, release_date, description, duration, f.mpa_id, mpa_name, " +
                            "d.DIRECTOR_ID as DIRECTOR_ID, d.DIRECTOR_NAME as DIRECTOR_NAME, fg.genre_id, genre_name " +
                            "from films_genres fg " +
                            "right join films f on f.film_id = fg.film_id " +
                            "left join genres g on g.genre_id = fg.genre_id " +
                            "left join DIRECTORS d on f.DIRECTOR_ID = d.DIRECTOR_ID " +
                            "join MPA on f.MPA_ID = MPA.MPA_ID " +
                            "left join FILMS_LIKES fl1 on f.film_id = fl1.film_id and fl1.USER_ID = ? " +
                            "left join FILMS_LIKES fl2 on f.film_id = fl2.film_id and fl2.USER_ID = ? " +
                            "where fl1.film_id is not null and fl2.film_id is not null " +
                            "order by f.LIKES_COUNT desc, f.FILM_ID " +
                            "limit ?;", filmsRowMapper, userId, friendId, count);
        } catch (EmptyResultDataAccessException e) {
            log.info(e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Collection<Film> searchFilms(String query, String by) {
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("select f.film_id, film_name, release_date, description," +
                " duration, f.mpa_id, mpa_name, d.DIRECTOR_ID as DIRECTOR_ID, d.DIRECTOR_NAME as DIRECTOR_NAME," +
                " fg.genre_id, genre_name " +
                "from films_genres fg " +
                "right join films f on f.film_id = fg.film_id " +
                "left join genres g on g.genre_id = fg.genre_id " +
                "left join DIRECTORS d on f.DIRECTOR_ID = d.DIRECTOR_ID " +
                "join MPA on f.MPA_ID = MPA.MPA_ID ");
        if (by.equals("director")) {
            sqlQuery.append("where lower(DIRECTOR_NAME) like lower('%").append(query).append("%') ");
        } else if (by.equals("title")) {
            sqlQuery.append("where lower(FILM_NAME) like lower('%").append(query).append("%') ");
        } else {
            sqlQuery.append("where lower(FILM_NAME) like lower('%").append(query).append("%') ")
                    .append("or lower(DIRECTOR_NAME) like lower('%").append(query).append("%') ");
        }
        sqlQuery.append("order by LIKES_COUNT desc;");
        try {
            return jdbcTemplate.queryForObject(sqlQuery.toString(), filmsRowMapper);
        } catch (EmptyResultDataAccessException e) {
            log.info(e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Collection<Film> getFilmsOfDirector(int directorId, String sortBy) {
        String sortKey;
        if (sortBy.equals("likes")) {
            sortKey = "LIKES_COUNT";
        } else {
            sortKey = "YEAR(release_date)";
        }
        try {
            return jdbcTemplate.queryForObject(
                    "select f.film_id, film_name, release_date, description," +
                            " duration, f.mpa_id, mpa_name, d.DIRECTOR_ID as DIRECTOR_ID, d.DIRECTOR_NAME as DIRECTOR_NAME," +
                            " fg.genre_id, genre_name " +
                            "from films_genres fg " +
                            "right join films f on f.film_id = fg.film_id " +
                            "left join genres g on g.genre_id = fg.genre_id " +
                            "left join DIRECTORS d on f.DIRECTOR_ID = d.DIRECTOR_ID " +
                            "join MPA on f.MPA_ID = MPA.MPA_ID " +
                            "where d.DIRECTOR_ID = ? " +
                            "order by " + sortKey + ";", filmsRowMapper, directorId);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    private void addDirectorToFilm(Film film) {
        if (!film.getDirectors().isEmpty()) {
            int directorId = film.getDirectors().get(0).getId();
            jdbcTemplate.update("update FILMS set DIRECTOR_ID = ? where FILM_ID = ?", directorId, film.getId());
            String directorName = jdbcTemplate.queryForObject("select DIRECTOR_NAME from DIRECTORS where DIRECTOR_ID = ?;",
                    (rs, rowNum) -> rs.getString("DIRECTOR_NAME"), directorId);
            film.getDirectors().get(0).setName(directorName);
            log.info("В информацию о фильме с id = {} добавлен режиссер с id = {}", film.getId(), directorId);
        } else {
            jdbcTemplate.update("update FILMS set DIRECTOR_ID = ? where FILM_ID = ?", null, film.getId());
        }
    }
}
