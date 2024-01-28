package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testGet() {
        Map<Integer, String> mpaMap = Map.of(1, "G", 2, "PG", 3, "PG-13", 4, "R", 5, "NC-17");
        Collection<Mpa> mpas = new ArrayList<>();
        mpaMap.forEach((k, v) -> mpas.add(new Mpa(k, v)));
        Collection<Mpa> sortedMpas = mpas.stream().sorted(Comparator.comparingInt(Mpa::getId)).collect(Collectors.toList());
        MpaStorage mpaStorage = new MpaDbStorage(jdbcTemplate);
        Collection<Mpa> savedMpas = mpaStorage.getAll();
        Mpa savedMpa = mpaStorage.getById(1);
        Assertions.assertThat(savedMpa)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(new Mpa(1, "G"));

        Assertions.assertThat(savedMpas)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(sortedMpas);
    }
}
