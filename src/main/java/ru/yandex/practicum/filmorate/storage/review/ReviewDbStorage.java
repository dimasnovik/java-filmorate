package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InvalidValueException;
import ru.yandex.practicum.filmorate.exception.NoSuchReviewException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.daoUtils.IDValidator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;


@Component
@Log4j2
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final IDValidator validator;

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
        validateId(review);
        validator.validateFilmId(review.getFilmId());
        validator.validateUserId(review.getUserId());

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
        validator.validateFilmId(review.getFilmId());
        validator.validateUserId(review.getUserId());

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
        validator.validateFilmId(reviewId);
        validator.validateUserId(userId);

        try {
            String sql = "INSERT INTO review_opinion (review_id, user_id, is_like) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, reviewId, userId, isLike);
        } catch (DuplicateKeyException e) {
            throw new NoSuchReviewException("review error: review already have like/dislike");
        }
    }

    @Override
    public Collection<Review> getReviewsByFilmId(int filmId, int count) {
        String sql = "SELECT r.review_id, r.film_id, r.user_id, r.is_positive, r.content, " +
                "(SELECT COUNT(ro.review_id) FILTER (WHERE ro.is_like = true) - COUNT(ro.review_id) FILTER (WHERE ro.is_like = false) " +
                "FROM review_opinion ro " +
                "WHERE ro.review_id = r.review_id) AS useful " +
                "FROM reviews r " +
                "WHERE r.film_id = ? " +
                "ORDER BY useful DESC " +
                "LIMIT ?;";

        List<Review> reviews = new ArrayList<>();

        jdbcTemplate.query(sql, (resultSet, i) -> {
            reviews.add(rsToReview(resultSet));
            return rsToReview(resultSet);
        }, filmId, count);

        return reviews;
    }

    @Override
    public void removeReview(int reviewId) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        Review review = getReviewById(reviewId);

        if (review != null) {
            jdbcTemplate.update(sql, reviewId);
        } else {
            throw new RuntimeException("Ошибка при удалении отзыва.");
        }
    }

    @Override
    public Collection<Review> getReviewsSortByUseful(int count) {
        String sql = "SELECT r.review_id, r.film_id, r.user_id, r.is_positive, r.content, " +
                "(SELECT COUNT(ro.review_id) FILTER (WHERE ro.is_like = true) - COUNT(ro.review_id) FILTER (WHERE ro.is_like = false) " +
                "FROM review_opinion ro " +
                "WHERE ro.review_id = r.review_id) AS useful " +
                "FROM reviews r " +
                "ORDER BY useful DESC " +
                "LIMIT ?;";
        List<Review> reviews = new ArrayList<>();

        jdbcTemplate.query(sql, (resultSet, i) -> {
            reviews.add(rsToReview(resultSet));
            return rsToReview(resultSet);
        }, count);

        return reviews;
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

    private void validateId(Review review) {
        if (review.getFilmId() <= 0 || review.getUserId() <= 0) {
            throw new NoSuchElementException("review error: film id or user id can not be <= 0");
        }
    }
}
