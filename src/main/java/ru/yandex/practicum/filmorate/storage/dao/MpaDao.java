package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;


public interface MpaDao {
    Mpa getById(int id);

    Collection<Mpa> getAll();
}
