package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MpaService {
    private final MpaStorage mpaStorage;

    public Mpa getById(int id) {
        return mpaStorage.getById(id);
    }

    public Collection<Mpa> getAll() {
        return mpaStorage.getAll();
    }
}
