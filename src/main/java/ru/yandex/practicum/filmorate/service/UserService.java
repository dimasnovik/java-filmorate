package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public User create(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя изменено на логин - " + user.getLogin());
        }
        return storage.add(user);
    }

    public User update(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя изменено на логин - " + user.getLogin());
        }
        return storage.update(user);
    }

    public Collection<User> getAll() {
        return storage.getAll();
    }

    public User getById(int id) {
        return storage.getById(id);
    }

    public void addToFriends(int id1, int id2) {
        User user1 = storage.getById(id1);
        User user2 = storage.getById(id2);
        if (user1.getFriends().add(id2)) {
            user2.getFriends().add(id1);
            log.info(String.format("Пользователи с id = %d и id = %d теперь друзья", id1, id2));
        } else {
            log.info(String.format("Пользователи с id = %d и id = %d уже друзья", id1, id2));
        }
    }

    public void removeFromFriends(int id1, int id2) {
        User user1 = storage.getById(id1);
        User user2 = storage.getById(id2);
        if (user1.getFriends().remove(id2)) {
            user2.getFriends().remove(id1);
            log.info(String.format("Пользователи с id = %d и id = %d больше не друзья", id1, id2));
        } else {
            log.info(String.format("Пользователи с id = %d и id = %d и так не друзья", id1, id2));
        }
    }

    public List<User> getFriendsOfUser(Integer userId) {
        List<User> friends = new ArrayList<>();
        User user = storage.getById(userId);
        for (Integer friendId : user.getFriends()) {
            friends.add(storage.getById(friendId));
        }
        return friends;
    }

    public List<User> getCommonFriends(int id1, int id2) {
        User user1 = storage.getById(id1);
        User user2 = storage.getById(id2);
        List<User> commonFriends = new ArrayList<>();
        Set<Integer> commonFriendsIds = new HashSet<>(user1.getFriends());
        commonFriendsIds.retainAll(user2.getFriends());
        for (Integer commonFriendsId : commonFriendsIds) {
            commonFriends.add(storage.getById(commonFriendsId));
        }
        return commonFriends;
    }
}
