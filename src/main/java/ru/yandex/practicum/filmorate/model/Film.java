package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.TreeSet;

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

    private final TreeSet<Genre> genres = new TreeSet<>();

    private int rate;
    @NotNull
    private final Mpa mpa;
}
