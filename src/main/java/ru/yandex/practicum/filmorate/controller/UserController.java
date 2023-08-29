package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidValueException;
import ru.yandex.practicum.filmorate.exception.NoSuchUserException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @GetMapping
    public List<User> listUsers() {
        return List.copyOf(users.values());
    }

    @PostMapping
    public User create(@RequestBody User newUser) {
        validateUser(newUser);

        String email = newUser.getEmail();
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                log.warn("Пользователь с электронной почтой " +
                        email + " уже зарегистрирован.");
                throw new UserAlreadyExistException("Пользователь с электронной почтой " +
                        email + " уже зарегистрирован.");
            }
        }
        newUser.setId(nextId);
        users.put(newUser.getId(), newUser);
        nextId++;
        log.info("Пользователь \"" + newUser.getName() + "\" добавлен");
        return newUser;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        validateUser(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Пользователь \"" + user.getName() + "\" изменен");
        } else {
            throw new NoSuchUserException("Пользователя с id = " + user.getId() + " не существует");
        }
        return user;
    }


    private void validateUser(User user) {
        String email = user.getEmail();
        if (!(Pattern.matches("^[\\w-.]+@([\\w-]+.)+[\\w-]{2,4}$", email))) {
            log.warn("Неверный формат email, переданный email: " + email);
            throw new InvalidValueException("Неверный формат email, переданный email: " + email);
        }

        String login = user.getLogin();
        if (login.isBlank() || login.contains(" ")) {
            log.warn("Неверный формат login, login содержит пробелы или пуст");
            throw new InvalidValueException("Неверный формат login, login содержит пробелы или пуст");
        }
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(login);
            log.info("Имя пользователя изменено на логин - " + login);
        }
        LocalDate birthday = user.getBirthday();

        if (birthday.isAfter(LocalDate.now())) {
            log.warn("Дата рождения не может быть в будущем");
            throw new InvalidValueException("Дата рождения не может быть в будущем");
        }

    }
}
