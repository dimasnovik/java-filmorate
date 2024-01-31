package ru.yandex.practicum.filmorate.storage.director;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Director;
import org.assertj.core.api.Assertions;

import java.util.Collection;
import java.util.List;

@JdbcTest
@AllArgsConstructor(onConstructor_ = @Autowired)
class DirectorDbStorageTest {
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    public void resetDb() {
        jdbcTemplate.execute("ALTER TABLE DIRECTORS ALTER COLUMN DIRECTOR_ID RESTART WITH 1;");
    }

    @Test
    void addAndGet() {
        Director dir1 = new Director(1,"Dir1");
        Director dir2 = new Director(2,"Dir2");
        DirectorStorage directorStorage = new DirectorDbStorage(jdbcTemplate);
        directorStorage.add(dir1);
        directorStorage.add(dir2);

        Director savedDir = directorStorage.getById(1);

        Assertions.assertThat(savedDir)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(dir1);

        Collection<Director> savedDirs = directorStorage.getAll();

        Assertions.assertThat(savedDirs)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(dir1,dir2));

    }

    @Test
    void update() {
        Director dir1 = new Director(1,"Dir1");
        Director dir2 = new Director(1,"Dir2");
        DirectorStorage directorStorage = new DirectorDbStorage(jdbcTemplate);
        directorStorage.add(dir1);
        directorStorage.update(dir2);
        Director savedDir = directorStorage.getById(1);

        Assertions.assertThat(savedDir)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(dir2);
    }

    @Test
    void remove() {
        Director dir1 = new Director(1,"Dir1");
        Director dir2 = new Director(2,"Dir2");
        DirectorStorage directorStorage = new DirectorDbStorage(jdbcTemplate);
        directorStorage.add(dir1);
        directorStorage.add(dir2);
        directorStorage.remove(1);

        Collection<Director> savedDirs = directorStorage.getAll();

        Assertions.assertThat(savedDirs)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(dir2));
    }
}