package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(Long id, Long friendId) {
        log.debug("Попытка добавить друга: userId={}, friendId={}", id, friendId);

        User user = userStorage.findById(id);
        User friend = userStorage.findById(friendId);
        if (user == null || friend == null) {
            log.error("Ошибка: Один из пользователей не найден (userId={}, friendId={})", id, friendId);
            throw new NotFoundException("Один из пользователей не найден");
        }
        if (id.equals(friendId)) {
            log.warn("Попытка добавить самого себя в друзья (userId={})", id);
            throw new ValidationException("Нельзя добавить самого себя");
        }
        userStorage.addNewFriend(user.getId(), friend.getId());

        log.info("Пользователь {} добавил в друзья пользователя {}", id, friendId);
        return user;
    }

    public Set<Long> getFriends(Long id) {
        log.debug("Запрос списка друзей пользователя (userId={})", id);

        User user = userStorage.findById(id);
        if (user == null) {
            log.error("Ошибка: Пользователь не найден (userId={})", id);
            throw new NotFoundException("Пользователь не найден");
        }

        Set<Long> friends = userStorage.getFriends(user.getId());
        log.info("Пользователь {} имеет {} друзей", id, friends.size());
        return friends;
    }

    public void deleteFriend(Long userId, Long friendId) {
        log.debug("Попытка удаления друга (userId={}, friendId={})", userId, friendId);

        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        if (friend == null || user == null ||
                user.getId() == null || friend.getId() == null) {
            log.error("Ошибка: Пользователь не найден (userId={}, friendId={})", userId, friendId);
            throw new NotFoundException("Пользователь не найден");
        }

        userStorage.deleteFriend(userId, friendId);

        log.info("Пользователь {} удалил из друзей {}", userId, friendId);
    }

    public Set<Long> findCommonFriends(Long id, Long friendId) {
        log.debug("Запрос общих друзей (userId={}, friendId={})", id, friendId);

        User user = getUserById(id);
        User friend = getUserById(friendId);
        Set<Long> users = userStorage.getFriends(user.getId());
        Set<Long> friends = userStorage.getFriends(friend.getId());
        Set<Long> commonFriends = users.stream()
                .filter(friends::contains)
                .collect(Collectors.toSet());

        log.info("Найдено {} общих друзей между userId={} и friendId={}", commonFriends.size(), id, friendId);
        return commonFriends;
    }

    public User getUserById(Long id) {
        log.debug("Запрос информации о пользователе (userId={})", id);
        User user = userStorage.findById(id);
        if (user == null) {
            log.error("Ошибка: Пользователь не найден (userId={})", id);
            throw new NotFoundException("Пользователь не найден");
        }
        return user;
    }

    public User updateUser(User user) {
        log.debug("Попытка обновить пользователя: {}", user);

        User existing = userStorage.findById(user.getId());
        if (existing == null) {
            log.warn("Пользователь с id={} не найден", user.getId());
            throw new NotFoundException("Пользователь не найден");
        }
        return userStorage.updateUser(user);
    }
}