package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchUserException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@Primary
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<List<Film>> filmsRowMapper;
    private final RowMapper<User> userRowMapper;

    @Override
    public User add(User user) {
        if (!jdbcTemplate.query(
                "select * from USERS where EMAIL = ?", userRowMapper, user.getEmail()).isEmpty()) {
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

        jdbcTemplate.update("update USERS set " +
                        "EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? where USER_ID = ?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday().toString(), id);

        log.info(String.format("Пользователь %s с id = %d изменен", user.getLogin(), user.getId()));
        return user;
    }

    @Override
    public void deleteById(int id) {
        jdbcTemplate.update("delete from USERS where USER_ID = ?", id);
        log.info(String.format("Пользователь с id = %d удален", id));
    }

    @Override
    public User getById(int id) {
        return jdbcTemplate.queryForObject("select * from USERS where USER_ID = ?", userRowMapper, id);
    }

    @Override
    public Collection<User> getAll() {
        return jdbcTemplate.query("select * from USERS", userRowMapper);
    }

    @Override
    public Collection<User> getFriends(int id) {
        return jdbcTemplate.query(
                "select user_id, email, login, name, birthday " +
                        "from friends f " +
                        "join users u on f.user2_id = u.user_id " +
                        "where f.user1_id = ?;", userRowMapper, id);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        jdbcTemplate.update("insert into FRIENDS(USER1_ID, USER2_ID) VALUES (?,?)", userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        jdbcTemplate.update("delete from FRIENDS where USER1_ID = ? and USER2_ID = ?", userId, friendId);
    }

    @Override
    public Collection<User> getCommonFriends(int id1, int id2) {
        return jdbcTemplate.query(
                "select f.user2_id as user_id, email, login, name, birthday " +
                        "from friends f " +
                        "join friends ff on f.user2_id = ff.user2_id " +
                        "join users u on u.user_id = f.user2_id " +
                        "where f.user1_Id = ? and ff.user1_id = ?;", userRowMapper, id1, id2);
    }

    @Override
    public Collection<Film> getRecommend(int id) {
        List<Integer> likes = jdbcTemplate.query("SELECT FILM_ID FROM FILMS_LIKES WHERE USER_ID = ?",
                ((rs, rowNum) -> rs.getInt("FILM_ID")), id);
        if (likes.isEmpty()) {
            return List.of();
        }
        String sqlForSearchUser = "SELECT USER_ID, COUNT(FILM_ID) AS SUMS  FROM FILMS_LIKES fl WHERE " +
                "(FILM_ID IN (SELECT FILM_ID FROM FILMS_LIKES fl2 WHERE USER_ID = ?) AND USER_ID <> ?)" +
                "GROUP BY USER_ID ORDER BY SUMS DESC LIMIT 1";
        Integer idUserWithSomeLikes;
        List<Film> films;
        try {
            idUserWithSomeLikes = jdbcTemplate.queryForObject(sqlForSearchUser, (rs, rowNum) ->
                    rs.getInt("USER_ID"), id, id);
            String sqlForSearchRecommend = "select f.film_id, film_name, release_date, description," +
                    " duration, f.mpa_id, mpa_name, d.DIRECTOR_ID as DIRECTOR_ID, d.DIRECTOR_NAME as DIRECTOR_NAME," +
                    " fg.genre_id, genre_name " +
                    "from films_genres fg " +
                    "right join films f on f.film_id = fg.film_id " +
                    "left join genres g on g.genre_id = fg.genre_id " +
                    "left join DIRECTORS d on f.DIRECTOR_ID = d.DIRECTOR_ID " +
                    "join MPA on f.MPA_ID = MPA.MPA_ID  WHERE f.FILM_ID IN (SELECT FILM_ID FROM FILMS_LIKES fl " +
                    "WHERE (USER_ID = ? AND FILM_ID NOT IN (SELECT FILM_ID FROM FILMS_LIKES fl2 WHERE USER_ID = ?)))" +
                    "ORDER BY f.FILM_ID";
            films = jdbcTemplate.queryForObject(sqlForSearchRecommend, filmsRowMapper, idUserWithSomeLikes, id);
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
        return films;
    }

    @Override
    public void validateId(int userId) {
        try {
            String sqlQuery = "SELECT user_id FROM users WHERE user_id = ?;";
            jdbcTemplate.queryForObject(sqlQuery, Integer.class, userId);
        } catch (DataAccessException e) {
            throw new NoSuchUserException(String.format("Пользователь с id = %d не найден.", userId));
        }
    }
}
