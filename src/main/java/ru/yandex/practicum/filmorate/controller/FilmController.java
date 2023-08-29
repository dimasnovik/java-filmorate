package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.InvalidValueException;
import ru.yandex.practicum.filmorate.exception.NoSuchFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;


    @GetMapping
    public List<Film> listUsers() {
        return List.copyOf(films.values());
    }

    @PostMapping
    public Film create(@RequestBody Film newFilm) {
        log.info("afafa");
        validateFilm(newFilm);
        for (Film film : films.values()) {
            if (film.equals(newFilm)) {
                throw new FilmAlreadyExistException("Фильм \"" + film.getName() + "\" уже добавлен");
            }
        }
        newFilm.setId(nextId);
        films.put(newFilm.getId(), newFilm);
        nextId++;

        log.info("Добавлен фильм: " + newFilm);
        return newFilm;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        validateFilm(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм \"" + film.getName() + "\" изменен");
            return film;
        } else {
            throw new NoSuchFilmException("Фильма с id = " + film.getId() + " не существует");
        }
    }

    private void validateFilm(Film film) {
        if (film.getName().isBlank()) {
            log.warn("Название фильма не может быть пустым");
            throw new InvalidValueException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Описание фильма не может быть больше 200 символов");
            throw new InvalidValueException("Описание фильма не может быть больше 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new InvalidValueException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма должна быть положительной");
            throw new InvalidValueException("Продолжительность фильма должна быть положительной");
        }
    }
}
