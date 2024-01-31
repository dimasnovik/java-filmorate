package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
public class Film {
    @EqualsAndHashCode.Exclude
    private int id;

    @NotBlank
    private final String name;

    @Size(max = 200)
    @NotNull
    private final String description;

    @NotNull
    private final LocalDate releaseDate;

    @Positive
    private final int duration;

    private final TreeSet<Genre> genres = new TreeSet<>();

    private int rate;
    @NotNull
    private final Mpa mpa;

    private List<Director> directors = new ArrayList<>();
}
