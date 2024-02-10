package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {
    Collection<Review> getReviews();

    Review addReview(Review review);

    Review getReviewById(long reviewId);

    Review update(Review review);

    void addOpinionToReview(int id, int userId, boolean isLike);

    Collection<Review> getReviewsByFilmId(int filmId, int count);

    void removeReview(int reviewId);

    Collection<Review> getReviewsSortByUseful(int count);
}
