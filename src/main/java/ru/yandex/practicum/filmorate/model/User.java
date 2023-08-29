package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class User {
    private int id;
    @NonNull
    private String email;
    @NonNull
    private final String login;
    private String name;
    @NonNull
    private LocalDate birthday;

}
