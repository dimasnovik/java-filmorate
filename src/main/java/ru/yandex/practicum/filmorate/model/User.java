package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    private int id;

    @Email
    @NotBlank
    private final String email;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]*$", message = "Пароль не должен содержать специальные символы и пробелы")
    private final String login;

    private String name;

    @PastOrPresent
    @NotNull
    private final LocalDate birthday;
}
