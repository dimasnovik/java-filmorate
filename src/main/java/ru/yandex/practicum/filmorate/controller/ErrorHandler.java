package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;

import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler({NoSuchElementException.class, NoSuchFilmException.class, NoSuchUserException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoSuchElement(final RuntimeException e) {
        log.debug("Получен статус 404 Not found {}", e.getMessage(), e);
        return Map.of("Wrong ID", e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidDate(final InvalidValueException e) {
        log.debug("Получен статус 400 Bad Request {}", e.getMessage(), e);
        return Map.of("Validation error", e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleUserAlreadyExistException(final UserAlreadyExistException e) {
        log.debug("Получен статус 409 Conflict {}", e.getMessage(), e);
        return Map.of("Wrong user", e.getMessage());
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public Map<String, String> handleOtherExceptions(final Throwable e) {
//        log.debug("Получен статус 500 Internal server error {}", e.getMessage(), e);
//        return Map.of("Непредвиденная ошибка", e.getMessage());
//    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoSuchReviewException(final NoSuchReviewException e) {
        log.debug("Получен статус 404 Not found {}", e.getMessage(), e);
        return Map.of("Wrong ID", e.getMessage());
    }
}
