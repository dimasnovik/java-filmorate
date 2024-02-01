package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Collection<Director> getAll() {
        return directorStorage.getAll();
    }

    public Director create(Director director) {
        return directorStorage.add(director);
    }

    public Director update(Director director) {
        return directorStorage.update(director);
    }

    public Director getById(int id) {
        return directorStorage.getById(id);
    }

    public void remove(int id) {
        directorStorage.remove(id);
    }
}
