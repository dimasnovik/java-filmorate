package ru.yandex.practicum.filmorate.storage.daoUtils;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FilmsRowMapper implements RowMapper<List<Film>> {

    @Override
    public List<Film> mapRow(ResultSet rs, int rowNum) throws SQLException {
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
    }
}
