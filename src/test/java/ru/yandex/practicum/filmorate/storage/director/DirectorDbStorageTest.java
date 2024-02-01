package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;
import org.assertj.core.api.Assertions;

import java.util.Collection;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DirectorDbStorageTest {
    private Director dir1;
    private final DirectorStorage directorStorage;

    @BeforeEach
    public void beforeEach() {
        dir1 = new Director(1,"Dir1");
        directorStorage.add(dir1);
    }

    @Test
    void addAndGet() {
        Director savedDir = directorStorage.getById(1);

        Assertions.assertThat(savedDir)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(dir1);

        Collection<Director> savedDirs = directorStorage.getAll();

        Assertions.assertThat(savedDirs)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(dir1));

    }

    @Test
    void update() {
        Director dir3 = new Director(1, "Dir3");
        directorStorage.update(dir3);
        Director savedDir = directorStorage.getById(1);
        Assertions.assertThat(savedDir)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(dir3);
    }

    @Test
    void remove() {
        directorStorage.remove(1);

        Collection<Director> savedDirs = directorStorage.getAll();

        Assertions.assertThat(savedDirs)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of());
    }
}