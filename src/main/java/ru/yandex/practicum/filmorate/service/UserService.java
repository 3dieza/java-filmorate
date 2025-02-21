package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.util.Validator.validateUser;

@Slf4j
@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    private long getNextId() {
        long currentMaxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }

    public User createUser(User user) {
        validateUser(user);
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
        validateUser(user);
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
}