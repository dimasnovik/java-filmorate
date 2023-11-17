package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    @Override
    public Film add(Film film) {
        film.setId(nextId);
        films.put(film.getId(), film);
        nextId++;

        log.info("Добавлен фильм: " + film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм \"" + film.getName() + "\" изменен");
            return film;
        } else {
            throw new NoSuchFilmException("Фильма с id = " + film.getId() + " не существует");
        }
    }

    @Override
    public Film getById(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new NoSuchFilmException("Фильма с id = " + id + " не существует");
        }
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }
}
