package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    public final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> friends = new HashMap<>();

    private long getNextId() {
        long currentMaxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }

    public User createUser(User user) {
        if (user.getId() == null || user.getId() == 0) {
            user.setId(getNextId());
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь с id: {}", user.getId());
        return user;
    }

    public User updateUser(User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.error("Пользователь с таким id не найден: {}", user.getId());
            throw new NotFoundException("[Пользователь с таким id не найден]");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь обновлен с id: {}", user.getId());
        return user;
    }

    public Collection<User> getUsers() {
        log.info("Выполнен запрос на получение всех пользователей");
        return users.values();
    }

    public User findById(Long id) {
        return users.get(id);
    }

    public Set<Long> getFriends(Long userId) {
        return friends.getOrDefault(userId, Collections.emptySet());
    }

    public void addNewFriend(Long userId, Long friendId) {
        friends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friends.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }
}