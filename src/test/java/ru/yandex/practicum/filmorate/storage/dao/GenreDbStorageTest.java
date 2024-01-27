package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testGet() {
        Map<Integer, String> genreMap = Map.of(1, "Комедия", 2, "Драма",
                3, "Мультфильм", 4, "Триллер", 5, "Документальный", 6, "Боевик");
        Collection<Genre> genres = new ArrayList<>();
        genreMap.forEach((k, v) -> genres.add(new Genre(k, v)));
        Collection<Genre> sortedGenres = genres.stream().sorted(Comparator.comparingInt(Genre::getId)).collect(Collectors.toList());
        GenreStorage genreStorage = new GenreDbStorage(jdbcTemplate);
        Collection<Genre> savedGenres = genreStorage.getAll();
        Genre savedGenre = genreStorage.getById(1);
        Assertions.assertThat(savedGenre)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(new Genre(1, "Комедия"));

        Assertions.assertThat(savedGenres)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(sortedGenres);
    }
}

