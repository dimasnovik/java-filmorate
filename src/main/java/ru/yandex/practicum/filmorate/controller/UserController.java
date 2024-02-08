package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    private final FeedService feedService;

    @GetMapping
    public Collection<User> getAll() {
        log.info(String.format("Получен GET запрос на адрес: %s", "/users"));
        return userService.getAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info(String.format("Получен POST запрос на адрес: %s", "/users"));
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info(String.format("Получен POST запрос на адрес: %s", "/users"));
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Integer id) {
        log.info(String.format("Получен DELETE запрос на адрес: %s/%d", "/users", id));
        userService.deleteById(id);
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable("id") Integer id) {
        log.info(String.format("Получен GET запрос на адрес: %s/%d", "/users", id));
        return userService.getById(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriendsOfUser(@PathVariable("id") Integer id) {
        log.info(String.format("Получен GET запрос на адрес: %s/%d/friends", "/users", id));
        return userService.getFriendsOfUser(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        log.info(String.format("Получен PUT запрос на адрес: %s/%d/friends/%d", "/users", id, friendId));
        userService.addToFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        log.info(String.format("Получен DELETE запрос на адрес: %s/%d/friends/%d", "/users", id, friendId));
        userService.removeFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable("id") Integer id, @PathVariable("otherId") Integer otherId) {
        log.info(String.format("Получен GET запрос на адрес: %s/%d/friends/common/%d", "/users", id, otherId));
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<Film> getRecommend(@PathVariable("id") Integer id) {
        log.info(String.format("Получен GET запрос на адрес: %s/%d/recommendations", "/users", id));
        return userService.getRecommend(id);
    }

    @GetMapping("{id}/feed")
    public Collection<Feed> getFeed(@PathVariable int id) {
        return feedService.getFeed(id);
    }
}
