package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InvalidValueException;
import ru.yandex.practicum.filmorate.exception.NoSuchFilmException;
import ru.yandex.practicum.filmorate.exception.NoSuchReviewException;
import ru.yandex.practicum.filmorate.exception.NoSuchUserException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


@Component
@Log4j2
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Review> getReviews() {
        String sql = "SELECT * FROM reviews";
        List<Review> reviews = new ArrayList<>();

        jdbcTemplate.query(sql, (resultSet, i) -> {
            reviews.add(rsToReview(resultSet));
            return rsToReview(resultSet);
        });

        return reviews;
    }

    @Override
    public Review addReview(Review review) {
        validateReview(review);
        assertFilmExists(review.getFilmId());
        assertUserExists(review.getUserId());

        String sql = "INSERT INTO reviews (film_id, user_id, is_positive, content) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, review.getFilmId());
            ps.setInt(2, review.getUserId());
            ps.setBoolean(3, review.isPositive());
            ps.setString(4, review.getContent());
            return ps;
        }, keyHolder);

        review.setReviewId(keyHolder.getKey().intValue());
        return getReviewById(keyHolder.getKey().intValue());
    }

    @Override
    public Review getReviewById(long reviewId) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rsToReview(rs), reviewId);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchReviewException("review was not found");
        }
    }

    @Override
    public Review update(Review review) {
        if (review.getFilmId() <= 0 || review.getUserId() <= 0) {
            throw new InvalidValueException("value <= 0");
        }
        assertFilmExists(review.getFilmId());
        assertUserExists(review.getUserId());

        int id = review.getReviewId();

        String sql = "UPDATE reviews SET is_positive = ?, content = ? WHERE review_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql, review.getIsPositive(), review.getContent(), id);
        if (rowsUpdated <= 0) {
            throw new NoSuchReviewException("review was not found");
        }

        return getReviewById(id);
    }

    @Override
    public void addOpinionToReview(int reviewId, int userId, boolean isLike) {
        assertFilmExists(reviewId);
        assertUserExists(userId);
        try {
            String sql = "INSERT INTO review_opinion (review_id, user_id, is_like) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, reviewId, userId, isLike);
        } catch (DuplicateKeyException e) {
            throw new NoSuchReviewException("review error: review already have like/dislike");
        }
    }

    @Override
    public Collection<Review> getReviewsByFilmId(int filmId) {
        String sql = "SELECT * FROM reviews WHERE film_id = ?";
        List<Review> reviews = new ArrayList<>();

        jdbcTemplate.query(sql, (resultSet, i) -> {
            reviews.add(rsToReview(resultSet));
            return rsToReview(resultSet);
        }, filmId);

        return reviews;
    }

    @Override
    public void removeReview(int reviewId) {
        String sql;
        Review review = getReviewById(reviewId);

        if (review != null) {
            sql = "DELETE FROM reviews WHERE review_id = ?";
            jdbcTemplate.update(sql, reviewId);
        } else {
            throw new RuntimeException("Ошибка при удалении отзыва.");
        }
    }

    private Review rsToReview(ResultSet rs) throws SQLException {
        Integer useful;
        Review review = new Review(rs.getInt("review_id"),
                rs.getString("content"),
                rs.getBoolean("is_positive"),
                rs.getInt("film_id"),
                rs.getInt("user_id"),
                0);

        String sql = "SELECT COUNT(review_id) FILTER (WHERE is_like = true) - COUNT(review_id) FILTER (WHERE is_like = false) " +
                "FROM review_opinion " +
                "WHERE review_id = ?;";

        useful = jdbcTemplate.queryForObject(sql, Integer.class, review.getReviewId());

        if (useful != null) {
            review.setUseful(useful);
        } else {
            throw new RuntimeException("Ошибка при расчете рейтинга отзыва.");
        }
        return review;
    }

    private void validateReview(Review review) {
        if (review.getContent() == null) {
            throw new InvalidValueException("review error: content is null");
        }

        if (review.getUserId() < 1) {
            throw new NoSuchElementException("review error: user id < 1");
        }


        if (review.getFilmId() < 1) {
            throw new NoSuchElementException("review error: film id < 1");
        }


        if (review.getIsPositive() == null) {
            throw new InvalidValueException("review error: is_positive is null");
        }
    }

    private void assertUserExists(int userId) {
        try {
            String sqlQuery = "SELECT user_id FROM users WHERE user_id = ?";
            jdbcTemplate.queryForObject(sqlQuery, Integer.class, userId);
        } catch (DataAccessException e) {
            throw new NoSuchUserException("Ошибка при создании отзыва: пользователь не найден.");
        }
    }

    private void assertFilmExists(int filmId) {
        try {
            String sqlQuery = "SELECT film_id FROM films WHERE film_id = ?";
            jdbcTemplate.queryForObject(sqlQuery, Integer.class, filmId);
        } catch (DataAccessException e) {
            throw new NoSuchFilmException("Ошибка при создании отзыва: фильм не найден.");
        }
    }
}
