package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    Film getById(int id);

    Collection<Film> getAll();

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    Collection<Film> getPopular(int count);

    Collection<Integer> getLikes(int id);


    Collection<Film> getFilmsOfDirector(int directorId, String sortBy);

    Collection<Film> getCommonPopularFilms(int userId, int friendId, int count);

}
