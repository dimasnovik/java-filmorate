package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InvalidValueException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortBy;
import ru.yandex.practicum.filmorate.model.feed.EventOperation;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final FeedService feedService;
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;
    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    public Film create(Film film) {
        checkReleaseDate(film);
        filmStorage.add(film);
        genreStorage.saveGenresOfFilm(film);
        return film;
    }

    public Film update(Film film) {
        checkReleaseDate(film);
        filmStorage.validateId(film.getId());
        filmStorage.update(film);
        genreStorage.saveGenresOfFilm(film);
        return film;
    }

    public void deleteById(int id) {
        filmStorage.validateId(id);
        filmStorage.deleteById(id);
    }

    public Film getById(int id) {
        filmStorage.validateId(id);
        return filmStorage.getById(id);
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public void addLike(int filmId, int userId) {
        filmStorage.validateId(filmId);
        userStorage.validateId(userId);
        filmStorage.addLike(filmId, userId);
        feedService.createFeed(userId, EventType.LIKE, EventOperation.ADD, filmId);
    }

    public void removeLike(int filmId, int userId) {
        filmStorage.validateId(filmId);
        userStorage.validateId(userId);
        filmStorage.removeLike(filmId, userId);
        feedService.createFeed(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
    }

    public Collection<Film> getTopFilms(int count, Integer genreId, Integer year) {
        return filmStorage.getPopular(count, genreId, year);
    }

    public Collection<Integer> getLikes(int id) {
        filmStorage.validateId(id);
        return filmStorage.getLikes(id);
    }

    public Collection<Film> getCommonPopularFilms(int userId, int friendId, int count) {
        userStorage.validateId(userId);
        userStorage.validateId(friendId);
        return filmStorage.getCommonPopularFilms(userId, friendId, count);
    }

    public Collection<Film> getFilmsOfDirector(int directorId, SortBy sortBy) {
        directorStorage.validateId(directorId);
        return filmStorage.getFilmsOfDirector(directorId, sortBy);
    }

    public Collection<Film> searchFilms(String query, String by) {
        return filmStorage.searchFilms(query, by);
    }

    private void checkReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            log.warn("Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new InvalidValueException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
