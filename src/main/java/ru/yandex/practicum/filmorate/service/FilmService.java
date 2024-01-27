package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InvalidValueException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

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

    public void addLike(int filmId, int userId) {
        userStorage.getById(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        userStorage.getById(userId);
        filmStorage.removeLike(filmId, userId);

    }

    public Collection<Film> getTopFilms(int count) {
        return filmStorage.getPopular(count);
    }

    public Collection<Integer> getLikes(int id) {
        return filmStorage.getLikes(id);
    }

    private void checkReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            log.warn("Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new InvalidValueException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
