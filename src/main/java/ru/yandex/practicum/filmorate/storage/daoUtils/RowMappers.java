package ru.yandex.practicum.filmorate.storage.daoUtils;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.*;

import java.util.ArrayList;
import java.util.List;

public class RowMappers {
    public static RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User(rs.getString("EMAIL"), rs.getString("LOGIN"),
                    rs.getDate("BIRTHDAY").toLocalDate());
            user.setName(rs.getString("NAME"));
            user.setId(rs.getInt("USER_ID"));
            return user;
        };
    }

    public static RowMapper<List<Film>> filmsRowMapper() {
        return (rs, rowNum) -> {
            List<Film> films = new ArrayList<>();
            while (!rs.isAfterLast()) {
                int filmId = rs.getInt("FILM_ID");
                Film film = new Film(rs.getString("FILM_NAME"), rs.getString("DESCRIPTION"),
                        rs.getDate("RELEASE_DATE").toLocalDate(), rs.getInt("DURATION"),
                        new Mpa(rs.getInt("MPA_ID"), rs.getString("MPA_NAME")));
                film.setId(filmId);
                if (rs.getString("DIRECTOR_NAME") != null) {
                    film.getDirectors().add(new Director(rs.getInt("DIRECTOR_ID"), rs.getString("DIRECTOR_NAME")));
                }
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

    public static RowMapper<Director> directorRowMapper() {
        return (rs, rowNum) -> new Director(rs.getInt("DIRECTOR_ID"), rs.getString("DIRECTOR_NAME"));
    }
}
