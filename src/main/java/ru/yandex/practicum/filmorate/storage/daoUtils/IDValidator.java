package ru.yandex.practicum.filmorate.storage.daoUtils;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchUserException;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class IDValidator {
    private JdbcTemplate jdbcTemplate;

    public void validateUserId(int userId) {
        try {
            String sqlQuery = "SELECT user_id FROM users WHERE user_id = ?;";
            jdbcTemplate.queryForObject(sqlQuery, Integer.class, userId);
        } catch (DataAccessException e) {
            throw new NoSuchUserException(String.format("Пользователь с id = %d не найден.", userId));
        }
    }

    public void validateFilmId(int filmId) {
        try {
            String sqlQuery = "SELECT film_id FROM FILMS WHERE film_id = ?;";
            jdbcTemplate.queryForObject(sqlQuery, Integer.class, filmId);
        } catch (DataAccessException e) {
            throw new NoSuchUserException(String.format("Фильм с id = %d не найден.", filmId));
        }
    }

    public void validateReviewId(int reviewId) {
        try {
            String sqlQuery = "SELECT REVIEW_ID FROM REVIEWS WHERE REVIEW_ID = ?;";
            jdbcTemplate.queryForObject(sqlQuery, Integer.class, reviewId);
        } catch (DataAccessException e) {
            throw new NoSuchUserException(String.format("Ревью с id = %d не найден.", reviewId));
        }
    }

    public void validateDirectorId(int filmId) {
        try {
            String sqlQuery = "SELECT DIRECTOR_ID FROM DIRECTORS WHERE DIRECTOR_ID = ?;";
            jdbcTemplate.queryForObject(sqlQuery, Integer.class, filmId);
        } catch (DataAccessException e) {
            throw new NoSuchUserException(String.format("Режиссер с id = %d не найден.", filmId));
        }
    }
}
