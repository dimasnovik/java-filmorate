package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@Log4j2
@RestController()
@RequestMapping(value = "/reviews", produces = "application/json")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public Collection<Review> getReviews() {
        log.info("review list requested.");
        return reviewService.getReviews();
    }

    @PostMapping
    public Review addReview(@RequestBody Review review) {
        log.info("review-{} added.", review.getReviewId());
        return reviewService.addReview(review);
    }

    @PutMapping()
    public Review updateReview(@RequestBody Review review) {
        log.info("review-{} updated.", review.getReviewId());
        return reviewService.update(review);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable long id) {
        log.info("review-{} requested.", id);
        return reviewService.getReviewById(id);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public void addLikeToReview(@PathVariable int reviewId, @PathVariable int userId) {
        reviewService.addOpinionToReview(reviewId, userId, true);
        log.debug("review-{} add like from user-{}", reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable int reviewId, @PathVariable int userId) {
        reviewService.addOpinionToReview(reviewId, userId, false);
        log.debug("review-{} add dislike from user-{}", reviewId, userId);
    }
}
