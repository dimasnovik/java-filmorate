package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void resetDb() {
        jdbcTemplate.execute("ALTER TABLE USERS ALTER COLUMN USER_ID RESTART WITH 1;");
    }

    @Test
    public void testAddAndGetUserById() {

        User newUser = new User("user@email.ru", "vanya123", LocalDate.of(1990, 1, 1));

        newUser.setName("Ivan Petrov");
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.add(newUser);
        newUser.setId(1);

        User savedUser = userStorage.getById(1);

        Assertions.assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void testGetAllUsers() {
        User user1 = new User("user1@email.ru", "vanya123", LocalDate.of(1990, 1, 1));
        user1.setName("Ivan Petrov");
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.add(user1);
        user1.setId(1);

        User user2 = new User("user2@email.ru", "vanya1234", LocalDate.of(1990, 1, 1));
        user2.setName("Petr Ivanov");
        userStorage.add(user2);
        user2.setId(2);
        Collection<User> newUsers = List.of(user1, user2);
        Collection<User> savedUsers = userStorage.getAll();
        Assertions.assertThat(savedUsers)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUsers);
    }

    @Test
    public void testUpdateUser() {
        User user1 = new User("user1@email.ru", "vanya123", LocalDate.of(1990, 1, 1));
        user1.setName("Ivan Petrov");
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.add(user1);
        user1.setId(1);

        User user2 = new User("user2@email.ru", "vanya1234", LocalDate.of(1990, 1, 1));
        user2.setId(1);
        user2.setName("Petr Ivanov");
        userStorage.update(user2);

        User savedUser = userStorage.getById(1);

        Assertions.assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user2);
    }

    @Test
    public void testFriends() {
        User user1 = new User("user1@email.ru", "vanya123", LocalDate.of(1990, 1, 1));
        user1.setName("Ivan Petrov");
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.add(user1);
        user1.setId(1);

        User user2 = new User("user2@email.ru", "vanya1234", LocalDate.of(1990, 1, 1));
        user2.setName("Petr Ivanov");
        userStorage.add(user2);
        user2.setId(2);

        User user3 = new User("user3@email.ru", "vanya12345", LocalDate.of(1990, 1, 1));
        user3.setName("Zhorik Ivanov");
        userStorage.add(user3);
        user3.setId(3);
        userStorage.addFriend(2, 1);
        userStorage.addFriend(2, 3);

        Collection<User> friends = List.of(user1, user3);
        Collection<User> savedFriends = userStorage.getFriends(2);
        Assertions.assertThat(savedFriends)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(friends);
        userStorage.removeFriend(2, 1);

        Collection<User> newFriends = List.of(user3);
        Collection<User> newSavedFriends = userStorage.getFriends(2);
        Assertions.assertThat(newSavedFriends)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newFriends);

        userStorage.addFriend(1, 3);

        Collection<User> commons = List.of(user3);
        Collection<User> newCommons = userStorage.getCommonFriends(2, 1);
        Assertions.assertThat(newCommons)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(commons);

    }
}