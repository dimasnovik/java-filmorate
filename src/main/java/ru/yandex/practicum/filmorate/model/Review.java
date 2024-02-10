package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class Review {
    private int reviewId;

    @NotBlank(message = "Содержание отзыва не может быть пустым или null.")
    @Size(max = 2048, message = "Максимальная длина содержания отзыва составляет 2048 символов.")
    private final String content;

    @NotNull(message = "Оценка отзыва не может быть равна null.")
    private final Boolean isPositive;

    @NotNull(message = "Id фильма не может быть равен null.")
    private final Integer filmId;

    @NotNull(message = "Id пользователя не может быть равен null.")
    private final Integer userId;
    private int useful;

    @JsonProperty("isPositive")
    public boolean isPositive() {
        return isPositive;
    }

}