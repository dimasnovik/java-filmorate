package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
 import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;

    public Collection<Review> getReviews() {
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

    public void removeReview(int reviewId) {
        reviewStorage.removeReview(reviewId);
    }

    public void addOpinionToReview(int reviewId, int userId, boolean isLike) {
        reviewStorage.addOpinionToReview(reviewId, userId, isLike);
    }

    public Collection<Review> getReviewsSortByUseful() {
        return reviewStorage.getReviews().stream()
                .sorted(Comparator.comparing(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    public Collection<Review> getFilmReviewsSortedByUsefulness(int filmId, int count) {
        if (count == 0) {
            count = Integer.MAX_VALUE;
        }

        return reviewStorage.getReviewsByFilmId(filmId).stream()
                .sorted(Comparator.comparing(Review::getUseful).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
