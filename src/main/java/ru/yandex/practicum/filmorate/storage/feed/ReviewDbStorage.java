package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchFilmException;
import ru.yandex.practicum.filmorate.exception.NoSuchReviewException;
import ru.yandex.practicum.filmorate.exception.NoSuchUserException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


@Component
@Log4j2
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Review> getReviews() {
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

        return getReviewById(keyHolder.getKey().intValue());
    }

    @Override
    public Review getReviewById(long reviewId) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rsToReview(rs), reviewId);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchReviewException("film was not found");
        }
    }

    @Override
    public Review update(Review review) {
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
        String sql = "INSERT INTO review_opinion (review_id, user_id, is_like) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, isLike);
    }

    private Review rsToReview(ResultSet rs) throws SQLException {
        return Review.builder()
                .reviewId(rs.getInt("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .filmId(rs.getInt("film_id"))
                .userId(rs.getInt("user_id"))
                .build();
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
