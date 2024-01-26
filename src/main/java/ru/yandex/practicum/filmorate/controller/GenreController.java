package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public Collection<Genre> getAll() {
        log.info(String.format("Получен GET запрос на адрес: %s", "/genres"));
        return genreService.getAll();
    }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable("id") int id) {
        log.info(String.format("Получен GET запрос на адрес: %s/%d", "/genres", id));
        return genreService.getById(id);
    }
}
