package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.feed.EventOperation;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FeedService feedService;

    public Collection<Review> getReviews() {
        return reviewStorage.getReviews();
    }

    public Review addReview(Review review) {
        review = reviewStorage.addReview(review);
        feedService.createFeed(review.getUserId(), EventType.REVIEW, EventOperation.ADD, review.getReviewId());
        return review;
    }

    public Review getReviewById(long id) {
        return reviewStorage.getReviewById(id);
    }

    public Review update(Review review) {
        review = reviewStorage.update(review);
        feedService.createFeed(review.getUserId(), EventType.REVIEW, EventOperation.UPDATE, review.getReviewId());
        return review;
    }

    public void removeReview(int reviewId) {
        Review review = getReviewById(reviewId);
        feedService.createFeed(review.getUserId(), EventType.REVIEW, EventOperation.REMOVE, reviewId);
        reviewStorage.removeReview(reviewId);
    }

    public void addOpinionToReview(int reviewId, int userId, boolean isLike) {
        reviewStorage.addOpinionToReview(reviewId, userId, isLike);
    }

    public Collection<Review> getReviewsSortByUseful() {
        return reviewStorage.getReviewsSortByUseful(Integer.MAX_VALUE);
    }

    public Collection<Review> getFilmReviewsSortedByUsefulness(int filmId, int count) {
        if (count == 0) {
            count = Integer.MAX_VALUE;
        }

        return reviewStorage.getReviewsByFilmId(filmId, count);
    }
}
