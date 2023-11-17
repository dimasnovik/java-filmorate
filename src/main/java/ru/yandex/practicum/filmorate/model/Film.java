package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    @EqualsAndHashCode.Exclude
    private int id;

    @NotBlank
    private final String name;

    @Size(max = 200)
    private final String description;

    @PastOrPresent
    private final LocalDate releaseDate;

    @Positive
    private final int duration;

    private final Set<Integer> usersLiked = new HashSet<>();
}
