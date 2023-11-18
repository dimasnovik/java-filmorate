package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidValueException;
import ru.yandex.practicum.filmorate.exception.NoSuchFilmException;
import ru.yandex.practicum.filmorate.exception.NoSuchUserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.info(String.format("Получен GET запрос на адрес: %s", "/films"));
        return filmService.getAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info(String.format("Получен POST запрос на адрес: %s", "/films"));
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info(String.format("Получен GET запрос на адрес: %s", "/films"));
        return filmService.update(film);
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable("id") int id) {
        log.info(String.format("Получен GET запрос на адрес: %s/%d", "/films", id));
        return filmService.getById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        log.info(String.format("Получен PUT запрос на адрес: %s/%d/like/%d", "/films", id, userId));
        return filmService.addLike(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        log.info(String.format("Получен DELETE запрос на адрес: %s/%d/like/%d", "/films", id, userId));
        return filmService.removeLike(userId, id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(defaultValue = "10", required = false) int count) {
        log.info(String.format("Получен GET запрос на адрес: %s/%s", "/films", "popular"));
        return filmService.getTopFilms(count);
    }
}