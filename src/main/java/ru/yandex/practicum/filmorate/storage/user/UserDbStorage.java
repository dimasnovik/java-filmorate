package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchUserException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@Primary
@AllArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        if (!jdbcTemplate.query(
                "select * from USERS where EMAIL = ?", userRowMapper(), user.getEmail()).isEmpty()) {
            throw new UserAlreadyExistException("Пользователь с таким email уже существует");
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");
        Map<String, String> params = Map.of("EMAIL", user.getEmail(), "LOGIN", user.getLogin(),
                "NAME", user.getName(), "BIRTHDAY", user.getBirthday().toString());
        int id = simpleJdbcInsert.executeAndReturnKey(params).intValue();

        user.setId(id);
        log.info(String.format("Пользователь %s с id = %d добавлен", user.getLogin(), id));
        return user;
    }

    @Override
    public User update(User user) {
        int id = user.getId();
        List<User> users = jdbcTemplate.query("select * from USERS where USER_ID = ?", userRowMapper(), id);

        if (users.isEmpty()) {
            throw new NoSuchUserException("Нет пользователя с id = " + id);
        } else {
            jdbcTemplate.update("update USERS set " +
                            "EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? where USER_ID = ?",
                    user.getEmail(), user.getLogin(), user.getName(), user.getBirthday().toString(), id);
        }
        log.info(String.format("Пользователь %s с id = %d изменен", user.getLogin(), user.getId()));
        return user;
    }

    @Override
    public User getById(int id) {
        try {
            return jdbcTemplate.queryForObject("select * from USERS where USER_ID = ?", userRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchUserException("Нет пользователя с id = " + id);
        }
    }

    @Override
    public Collection<User> getAll() {
        return jdbcTemplate.query("select * from USERS", userRowMapper());
    }

    @Override
    public Collection<User> getFriends(int id) {
        return jdbcTemplate.query(
                "select user_id, email, login, name, birthday " +
                        "from friends f " +
                        "join users u on f.user2_id = u.user_id " +
                        "where f.user1_id = ?;", userRowMapper(), id);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        getById(userId);
        getById(friendId);
        jdbcTemplate.update("insert into FRIENDS(USER1_ID, USER2_ID) VALUES (?,?)", userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        jdbcTemplate.update("delete from FRIENDS where USER1_ID = ? and USER2_ID = ?", userId, friendId);
    }

    @Override
    public Collection<User> getCommonFriends(int id1, int id2) {
        return
                jdbcTemplate.query(
                        "select f.user2_id as user_id, email, login, name, birthday " +
                                "from friends f " +
                                "join friends ff on f.user2_id = ff.user2_id " +
                                "join users u on u.user_id = f.user2_id " +
                                "where f.user1_Id = ? and ff.user1_id = ?;", userRowMapper(), id1, id2);

    }


    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User(rs.getString("EMAIL"), rs.getString("LOGIN"),
                    rs.getDate("BIRTHDAY").toLocalDate());
            user.setName(rs.getString("NAME"));
            user.setId(rs.getInt("USER_ID"));
            return user;
        };
    }
}
