package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.feed.ReviewStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;

    public List<Review> getReviews() {
        return reviewStorage.getReviews();
    }

    public Review addReview(Review review) {
        return reviewStorage.addReview(review);
    }

    public Review getReviewById(long id) {
        return reviewStorage.getReviewById(id);
    }

    public Review update(Review review) {
        return reviewStorage.update(review);
    }

    public void addOpinionToReview(int reviewId, int userId, boolean isLike) {
        reviewStorage.addOpinionToReview(reviewId, userId, isLike);
    }
}
