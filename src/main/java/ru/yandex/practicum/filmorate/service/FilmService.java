package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final Map<Long, Set<Long>> likes = new HashMap<>();
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public boolean addLike(Long id, Long userId) {
        log.debug("Попытка добавить лайк: filmId={}, userId={}", id, userId);

        Film film = filmStorage.getFilmById(id);
        User user = userStorage.findById(userId);
        if (film == null || user == null) {
            log.error("Ошибка: Фильм или пользователь не найден (filmId={}, userId={})", id, userId);
            throw new NotFoundException("Film or user not found");
        }
        boolean existsLikes = likes.containsKey(film.getId())
                && likes.get(id).contains(user.getId());
        if (existsLikes) {
            log.warn("Пользователь уже поставил лайк фильму (filmId={}, userId={})", id, userId);
            throw new NotFoundException("Пользователь уже ставил лайк");
        }
        log.info("Лайк успешно добавлен (filmId={}, userId={})", id, userId);
        return likes.computeIfAbsent(id, k -> new HashSet<>()).add(userId);
    }

    public boolean deleteLike(Long filmId, Long userId) {
        log.debug("Попытка удалить лайк: filmId={}, userId={}", filmId, userId);

        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.findById(userId);
        if (film == null || user == null) {
            log.error("Ошибка: Фильм или пользователь не найден (filmId={}, userId={})", filmId, userId);
            throw new NotFoundException("Нет фильма или юзера");
        }
        boolean existsLikes = likes.containsKey(film.getId())
                && likes.get(filmId).contains(user.getId());
        if (!existsLikes) {
            log.warn("Попытка удалить лайк, которого нет (filmId={}, userId={})", filmId, userId);
            throw new ValidationException("У фильма нет лайков");
        }
        log.info("Лайк успешно удален (filmId={}, userId={})", filmId, userId);
        return removeLike(filmId, user.getId());
    }

    public Set<Long> getLikes(Long filmId) {
        log.debug("Запрос лайков фильма (filmId={})", filmId);
        return likes.getOrDefault(filmId, Collections.emptySet());
    }

    private boolean removeLike(Long filmId, Long userId) {
        Set<Long> userLikes = likes.getOrDefault(filmId, Collections.emptySet());

        if (userLikes.isEmpty()) {
            log.warn("Попытка удалить лайк у фильма без лайков (filmId={})", filmId);
            throw new NotFoundException("Нет лайков");
        }

        boolean removed = userLikes.remove(userId);
        if (!removed) {
            log.warn("Лайк пользователя {} не найден у фильма {}", userId, filmId);
            throw new ValidationException("Лайка не существует");
        }

        if (userLikes.isEmpty()) {
            likes.remove(filmId);
        }

        log.debug("Лайк удален (filmId={}, userId={})", filmId, userId);
        return true;
    }

    public List<Film> findPopularFilm(Long count) {
        long validCount = count <= 0 ? 10 : count;
        log.debug("Запрос популярных фильмов, количество: {}", validCount);

        List<Film> popularFilms = filmStorage.getFilms().stream()
                .sorted(Comparator.comparingInt((Film film) ->
                        likes.getOrDefault(film.getId(), Collections.emptySet()).size()).reversed())
                .limit(validCount)
                .toList();

        log.info("Найдено {} популярных фильмов", popularFilms.size());
        return popularFilms;
    }
}