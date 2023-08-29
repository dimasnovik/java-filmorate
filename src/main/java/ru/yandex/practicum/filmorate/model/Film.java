package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
public class Film {
    @EqualsAndHashCode.Exclude
    private int id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final int duration;
}
