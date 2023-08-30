package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidValueException;
import ru.yandex.practicum.filmorate.exception.NoSuchFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
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
    public Film create(@Valid @RequestBody Film newFilm) {
        checkReleaseDate(newFilm);
        newFilm.setId(nextId);
        films.put(newFilm.getId(), newFilm);
        nextId++;

        log.info("Добавлен фильм: " + newFilm);
        return newFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        checkReleaseDate(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм \"" + film.getName() + "\" изменен");
            return film;
        } else {
            throw new NoSuchFilmException("Фильма с id = " + film.getId() + " не существует");
        }
    }

    private void checkReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new InvalidValueException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
