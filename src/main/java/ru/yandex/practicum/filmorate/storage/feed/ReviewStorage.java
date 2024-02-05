package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    List<Review> getReviews();

    Review addReview(Review review);

    Review getReviewById(long reviewId);

    Review update(Review review);

    void addOpinionToReview(int id, int userId, boolean isLike);
}
