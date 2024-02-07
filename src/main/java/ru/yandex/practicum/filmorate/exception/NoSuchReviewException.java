package ru.yandex.practicum.filmorate.exception;

public class NoSuchReviewException extends RuntimeException {
    public NoSuchReviewException(String message) {
        super(message);
    }
}
