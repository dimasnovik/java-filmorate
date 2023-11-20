package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private int id;

    @Email
    @NotBlank
    private final String email;

    @NotBlank
    private final String login;

    private String name;

    @PastOrPresent
    @NotNull
    private final LocalDate birthday;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<Integer> friends = new HashSet<>();
}
