package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NoSuchUserException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    private final Set<String> emails = new HashSet<>();
    private int nextId = 1;

    @GetMapping
    public List<User> listUsers() {
        return List.copyOf(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User newUser) {
        String name = newUser.getName();
        if (name == null || name.isBlank()) {
            newUser.setName(newUser.getLogin());
            log.info("Имя пользователя изменено на логин - " + newUser.getLogin());
        }
        String email = newUser.getEmail();
        if (emails.contains(email)) {
            log.warn("Пользователь с электронной почтой " +
                    email + " уже зарегистрирован.");
            throw new UserAlreadyExistException("Пользователь с электронной почтой " +
                    email + " уже зарегистрирован.");
        }
        newUser.setId(nextId);
        users.put(newUser.getId(), newUser);
        emails.add(email);
        nextId++;
        log.info("Пользователь \"" + newUser.getName() + "\" добавлен");
        return newUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя изменено на логин - " + user.getLogin());
        }
        if (users.containsKey(user.getId())) {
            emails.remove(users.get(user.getId()).getEmail());
            users.put(user.getId(), user);
            emails.add(user.getEmail());
            log.info("Пользователь \"" + user.getName() + "\" изменен");
        } else {
            throw new NoSuchUserException("Пользователя с id = " + user.getId() + " не существует");
        }
        return user;
    }
}
