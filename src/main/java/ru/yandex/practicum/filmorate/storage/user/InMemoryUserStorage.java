package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchUserException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    private final Set<String> emails = new HashSet<>();
    private int nextId = 1;

    @Override
    public User add(User user) {
        String email = user.getEmail();
        if (emails.contains(email)) {
            log.warn("Пользователь с электронной почтой " + email + " уже зарегистрирован.");
            throw new UserAlreadyExistException("Пользователь с электронной почтой " + email + " уже зарегистрирован.");
        }
        user.setId(nextId);
        users.put(user.getId(), user);
        emails.add(email);
        nextId++;
        log.info("Пользователь \"" + user.getName() + "\" добавлен");
        return user;
    }

    @Override
    public User update(User user) {
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

    @Override
    public User getById(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NoSuchUserException("Пользователя с id = " + id + " не существует");
        }
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }


}
