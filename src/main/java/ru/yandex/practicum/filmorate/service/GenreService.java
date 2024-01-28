package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GenreService {
    private final GenreStorage genreStorage;

    public Genre getById(int id) {
        return genreStorage.getById(id);
    }

    public Collection<Genre> getAll() {
        return genreStorage.getAll();
    }
}
