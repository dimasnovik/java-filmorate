package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortBy;

import java.util.Collection;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    void deleteById(int id);

    Film getById(int id);

    Collection<Film> getAll();

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    Collection<Film> getPopular(int count, Integer genreId, Integer year);

    Collection<Integer> getLikes(int id);

    Collection<Film> getFilmsOfDirector(int directorId, SortBy sortBy);

    Collection<Film> getCommonPopularFilms(int userId, int friendId, int count);

    Collection<Film> searchFilms(String query, String by);

    void validateId(int id);
}
