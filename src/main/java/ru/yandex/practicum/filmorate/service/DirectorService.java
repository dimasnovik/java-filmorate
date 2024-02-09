package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.daoUtils.IDValidator;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DirectorService {
    private final DirectorStorage directorStorage;
    private final IDValidator validator;

    public Collection<Director> getAll() {
        return directorStorage.getAll();
    }

    public Director create(Director director) {
        return directorStorage.add(director);
    }

    public Director update(Director director) {
        validator.validateDirectorId(director.getId());
        return directorStorage.update(director);
    }

    public Director getById(int id) {
        validator.validateDirectorId(id);
        return directorStorage.getById(id);
    }

    public void remove(int id) {
        validator.validateDirectorId(id);
        directorStorage.remove(id);
    }
}
