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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

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
        Set<Long> likes = film.getLikes();
        boolean existsLikes = likes.contains(user.getId());
        if (existsLikes) {
            log.warn("Пользователь уже поставил лайк фильму (filmId={}, userId={})", id, userId);
            throw new NotFoundException("Пользователь уже ставил лайк");
        }
        log.info("Лайк успешно добавлен (filmId={}, userId={})", id, userId);
        return likes.add(userId);
    }

    public boolean deleteLike(Long filmId, Long userId) {
        log.debug("Попытка удалить лайк: filmId={}, userId={}", filmId, userId);

        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.findById(userId);
        if (film == null || user == null) {
            log.error("Ошибка: Фильм или пользователь не найден (filmId={}, userId={})", filmId, userId);
            throw new NotFoundException("Нет фильма или юзера");
        }
        Set<Long> likes = film.getLikes();
        boolean existsLikes = likes.contains(user.getId());
        if (!existsLikes) {
            log.warn("Попытка удалить лайк, которого нет (filmId={}, userId={})", filmId, userId);
            throw new ValidationException("У фильма нет лайков");
        }
        log.info("Лайк успешно удален (filmId={}, userId={})", filmId, userId);
        return likes.remove(userId);
    }

    public Set<Long> getLikes(Long filmId) {
        log.debug("Запрос лайков фильма (filmId={})", filmId);
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм не найден: id=" + filmId);
        }
        return Collections.unmodifiableSet(film.getLikes());
    }

    public List<Film> findPopularFilm(Long count) {
        long validCount = count <= 0 ? 10 : count;
        log.debug("Запрос популярных фильмов, количество: {}", validCount);

        Collection<Film> filmsCollection = filmStorage.getFilms();

        List<Film> filmsList = filmsCollection.stream()
                .filter(Objects::nonNull)
                .toList();

        Comparator<Film> likesComparator = Comparator.comparingInt(film ->
                film.getLikes() != null ? film.getLikes().size() : 0);

        List<Film> popularFilms = filmsList.stream()
                .sorted(likesComparator.reversed())
                .limit(validCount)
                .toList();

        log.info("Найдено {} популярных фильмов", popularFilms.size());
        return popularFilms;
    }
}