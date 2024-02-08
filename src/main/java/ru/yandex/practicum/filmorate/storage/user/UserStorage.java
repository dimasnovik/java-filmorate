package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User add(User user);

    User update(User user);

    void deleteById(int id);

    User getById(int id);

    Collection<User> getAll();

    Collection<User> getFriends(int id);

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    Collection<User> getCommonFriends(int id1, int id2);

    Collection<Film> getRecommend(int id);

}
