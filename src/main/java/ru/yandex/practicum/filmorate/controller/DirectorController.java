package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@RequestMapping("/directors")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> getAll() {
        log.info("Получен GET запрос на адрес: /directors");
        return directorService.getAll();
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        log.info("Получен POST запрос на адрес: /directors");
        return directorService.create(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        log.info("Получен PUT запрос на адрес: /directors");
        return directorService.update(director);
    }

    @GetMapping("/{id}")
    public Director getById(@Positive @PathVariable("id") int id) {
        log.info(String.format("Получен GET запрос на адрес: %s/%d", "/directors", id));
        return directorService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void remove(@Positive @PathVariable("id") int id) {
        log.info(String.format("Получен GET запрос на адрес: %s/%d", "/directors", id));
        directorService.remove(id);
    }
}
