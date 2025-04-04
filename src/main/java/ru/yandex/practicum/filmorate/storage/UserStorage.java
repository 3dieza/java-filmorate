package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    Collection<User> getUsers();

    User findById(Long id);

    Set<Long> getFriends(Long userId);

    void addNewFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);
}