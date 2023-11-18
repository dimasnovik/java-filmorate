package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InvalidValueException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        checkReleaseDate(film);
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        checkReleaseDate(film);
        return filmStorage.update(film);
    }

    public Film getById(int id) {
        return filmStorage.getById(id);
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film addLike(int userId, int filmId) {
        Film film = filmStorage.getById(filmId);
        userStorage.getById(userId);
        if (film.getUsersLiked().add(userId)) {
            log.info(String.format("Пользователь с id = %d поставил лайк фильму с id = %d", userId, filmId));
        } else {
            log.info(String.format("Пользователь с id = %d уже ставил лайк фильму с id = %d", userId, filmId));
        }
        return film;
    }

    public Film removeLike(int userId, int filmId) {
        Film film = filmStorage.getById(filmId);
        userStorage.getById(userId);
        if (film.getUsersLiked().remove(userId)) {
            log.info(String.format("Пользователь с id = %d убрал лайк фильму с id = %d", userId, filmId));
        } else {
            log.info(String.format("Пользователь с id = %d не ставил/уже убрал лайк фильму с id = %d", userId, filmId));
        }
        return film;
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.<Film>comparingInt(film -> film.getUsersLiked().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void checkReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            log.warn("Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new InvalidValueException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
