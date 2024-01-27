package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    @AfterEach
    public void resetDb() {
        jdbcTemplate.execute("ALTER TABLE FILMS ALTER COLUMN FILM_ID RESTART WITH 1;");
    }

    @Test
    public void testAddAndGetFilmById() {
        Film film1 = new Film("film1", "good film",
                LocalDate.of(1989, 10, 10), 120, new Mpa(1, "G"));
        film1.setId(1);
        FilmStorage filmStorage = new FilmDbStorage(jdbcTemplate, genreStorage);
        filmStorage.add(film1);

        Film savedFilm = filmStorage.getById(1);

        Assertions.assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film1);
    }

    @Test
    public void testUpdateFilm() {
        Film film1 = new Film("film1", "good film",
                LocalDate.of(1989, 10, 10), 120, new Mpa(1, "G"));
        film1.setId(1);
        FilmStorage filmStorage = new FilmDbStorage(jdbcTemplate, genreStorage);
        filmStorage.add(film1);
        film1.getGenres().add(new Genre(1, "Комедия"));
        filmStorage.update(film1);
        Film savedFilm = filmStorage.getById(1);

        Assertions.assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film1);
    }

    @Test
    public void testGetAllFilms() {
        Film film1 = new Film("film1", "good film",
                LocalDate.of(1989, 10, 10), 120, new Mpa(1, "G"));
        film1.setId(1);
        FilmStorage filmStorage = new FilmDbStorage(jdbcTemplate, genreStorage);
        filmStorage.add(film1);
        Film film2 = new Film("film2", "bad film",
                LocalDate.of(1989, 10, 10), 120, new Mpa(1, "G"));
        film2.setId(2);

        filmStorage.add(film2);
        Collection<Film> films = List.of(film1, film2);
        Collection<Film> savedFilms = filmStorage.getAll();

        Assertions.assertThat(savedFilms)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(films);
    }

    @Test
    public void testLikes() {
        Film film1 = new Film("film1", "good film",
                LocalDate.of(1989, 10, 10), 120, new Mpa(1, "G"));
        film1.setId(1);
        FilmStorage filmStorage = new FilmDbStorage(jdbcTemplate, genreStorage);
        filmStorage.add(film1);

        User user1 = new User("user1@email.ru", "vanya123", LocalDate.of(1990, 1, 1));
        user1.setName("Ivan Petrov");
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.add(user1);
        user1.setId(1);

        User user2 = new User("user2@email.ru", "vanya1234", LocalDate.of(1990, 1, 1));
        user2.setName("Petr Ivanov");
        userStorage.add(user2);
        user2.setId(2);

        filmStorage.addLike(1, 1);
        filmStorage.addLike(1, 2);
        Collection<Integer> likes = List.of(1, 2);
        Collection<Integer> savedLikes = filmStorage.getLikes(1);
        Assertions.assertThat(savedLikes)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(likes);

        filmStorage.removeLike(1, 1);
        Collection<Integer> newLikes = List.of(2);
        Collection<Integer> newSavedLikes = filmStorage.getLikes(1);
        Assertions.assertThat(newSavedLikes)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newLikes);

        Assertions.assertThat(filmStorage.getPopular(1))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film1));
    }

}
