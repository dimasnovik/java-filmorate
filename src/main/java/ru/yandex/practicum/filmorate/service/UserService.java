package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.feed.EventOperation;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {
    private final UserStorage storage;
    private final FeedService feedService;

    public User create(User user) {
        setNameIfBlank(user);
        return storage.add(user);
    }

    public User update(User user) {
        storage.validateId(user.getId());
        setNameIfBlank(user);
        return storage.update(user);
    }

    public void deleteById(int id) {
        storage.validateId(id);
        storage.deleteById(id);
        log.info("Пользователь с id = {} удален", id);
    }

    public Collection<User> getAll() {
        return storage.getAll();
    }

    public User getById(int id) {
        storage.validateId(id);
        return storage.getById(id);
    }

    public void addToFriends(int userId, int friendId) {
        storage.validateId(userId);
        storage.validateId(friendId);
        storage.addFriend(userId, friendId);
        log.info("Пользователь с id = {} добавил пользователя с id = {} в друзья", userId, friendId);
        feedService.createFeed(userId, EventType.FRIEND, EventOperation.ADD, friendId);
    }

    public void removeFromFriends(int userId, int friendId) {
        storage.validateId(userId);
        storage.validateId(friendId);
        storage.removeFriend(userId, friendId);
        log.info("Пользователь с id = {} удалил пользователя с id = {} из друзей", userId, friendId);
        feedService.createFeed(userId, EventType.FRIEND, EventOperation.REMOVE, friendId);
    }

    public Collection<User> getFriendsOfUser(Integer userId) {
        storage.validateId(userId);
        return storage.getFriends(userId);
    }

    public Collection<User> getCommonFriends(int id1, int id2) {
        storage.validateId(id1);
        storage.validateId(id2);
        return storage.getCommonFriends(id1, id2);
    }

    public Collection<Film> getRecommend(int id) {
        return storage.getRecommend(id);
    }

    private void setNameIfBlank(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя изменено на логин - {}", user.getLogin());
        }
    }
}
