package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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
