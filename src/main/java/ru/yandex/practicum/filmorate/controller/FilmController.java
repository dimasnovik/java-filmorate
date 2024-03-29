package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidValueException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortBy;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Получен GET запрос на адрес: /films");
        return filmService.getAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен POST запрос на адрес: /films");
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Получен PUT запрос на адрес: /films");
        return filmService.update(film);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@Positive @PathVariable("id") int id) {
        log.info(String.format("Получен DELETE запрос на адрес: %s/%d", "/films", id));
        filmService.deleteById(id);
    }

    @GetMapping("/{id}")
    public Film getById(@Positive @PathVariable("id") int id) {
        log.info(String.format("Получен GET запрос на адрес: %s/%d", "/films", id));
        return filmService.getById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@Positive @PathVariable("id") int id, @PathVariable("userId") int userId) {
        log.info(String.format("Получен PUT запрос на адрес: %s/%d/like/%d", "/films", id, userId));
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        log.info(String.format("Получен DELETE запрос на адрес: %s/%d/like/%d", "/films", id, userId));
        filmService.removeLike(id, userId);
    }

    @GetMapping("/{id}/like")
    public Collection<Integer> getLikesOfFilm(@Positive @PathVariable("id") int id) {
        log.info(String.format("Получен GET запрос на адрес: %s/%d/like", "/films", id));
        return filmService.getLikes(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@Positive @RequestParam(defaultValue = "10") int count,
                                       @RequestParam(required = false) Integer genreId,
                                       @Min(1895) @RequestParam(required = false) Integer year) {
        log.info("Получен GET запрос на адрес: /films/popular");
        return filmService.getTopFilms(count, genreId, year);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonPopularFilms(
            @Positive @RequestParam("userId") int userId,
            @Positive @RequestParam("friendId") int friendId,
            @Positive @RequestParam(defaultValue = "10") int count) {
        log.info(String.format("Получен GET запрос на адрес: /films/common?userId=%d&friendId=%d", userId, friendId));
        return filmService.getCommonPopularFilms(userId, friendId, count);
    }

    @GetMapping("/search")
    public Collection<Film> searchFilms(@NotBlank @RequestParam("query") String query, @RequestParam("by") String by) {
        if (!by.equals("director") && !by.equals("title") && !by.equals("title,director") && !by.equals("director,title")) {
            throw new InvalidValueException("Недопустимое значение параметра запроса by, должен быть director/title/director,title");
        }
        return filmService.searchFilms(query, by);
    }


    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsOfDirector(@Positive @PathVariable("directorId") int directorId,
                                               @RequestParam(defaultValue = "likes") SortBy sortBy) {
        log.info(String.format("Получен GET запрос на адрес: %s/%d", "/films/director", directorId));
        return filmService.getFilmsOfDirector(directorId, sortBy);
    }
}