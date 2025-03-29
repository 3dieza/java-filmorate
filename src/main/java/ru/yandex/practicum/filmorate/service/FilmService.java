package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ru.yandex.practicum.filmorate.util.Validator.validateFilm;

@Service
@Slf4j
public class FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    private final Set<Integer> validGenreIds = new HashSet<>();
    private final Set<Integer> validRatingIds = new HashSet<>();

    public FilmService(
            @Qualifier("userDbStorage") UserStorage userStorage,
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            JdbcTemplate jdbcTemplate
    ) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.jdbcTemplate = jdbcTemplate;
        loadGenres();
        loadRatings();
    }

    public boolean addLike(Long filmId, Long userId) {
        log.debug("Попытка добавить лайк: filmId={}, userId={}", filmId, userId);

        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.findById(userId);

        if (film == null || user == null) {
            log.error("Ошибка: Фильм или пользователь не найден (filmId={}, userId={})", filmId, userId);
            throw new NotFoundException("Фильм или пользователь не найден");
        }

        if (hasLike(filmId, userId)) {
            log.warn("Лайк уже существует: filmId={}, userId={}", filmId, userId);
            throw new ValidationException("Пользователь уже ставил лайк фильму");
        }

        String insertSql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(insertSql, filmId, userId);

        log.info("Лайк успешно добавлен (filmId={}, userId={})", filmId, userId);
        return true;
    }

    public boolean deleteLike(Long filmId, Long userId) {
        log.debug("Попытка удалить лайк: filmId={}, userId={}", filmId, userId);

        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.findById(userId);

        if (film == null || user == null) {
            throw new NotFoundException("Фильм или пользователь не найден");
        }

        if (!hasLike(filmId, userId)) {
            log.warn("Удаление несуществующего лайка (filmId={}, userId={})", filmId, userId);
            throw new ValidationException("У фильма нет лайка от пользователя");
        }

        String deleteSql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(deleteSql, filmId, userId);

        log.info("Лайк успешно удалён (filmId={}, userId={})", filmId, userId);
        return true;
    }

    public boolean hasLike(Long filmId, Long userId) {
        String sql = """
                    SELECT EXISTS(
                        SELECT 1
                        FROM film_likes
                        WHERE film_id = ? AND user_id = ?
                    )
                """;
        return jdbcTemplate.queryForObject(sql, Boolean.class, filmId, userId);
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
        long validCount = count == null || count <= 0 ? 10 : count;
        log.debug("Запрос популярных фильмов, количество: {}", validCount);

        String sql = """
                    SELECT f.*, COUNT(fl.user_id) AS likes_count
                    FROM film f
                    LEFT JOIN film_likes fl ON f.id = fl.film_id
                    GROUP BY f.id
                    ORDER BY likes_count DESC
                    LIMIT ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            FilmDbStorage storage = (FilmDbStorage) filmStorage; // приведение
            Film film = storage.mapRowToFilm(rs);
            film.setLikes(getLikesByFilmId(film.getId()));
            film.setGenres(storage.getGenres(film.getId()));
            return film;
        }, validCount);
    }

    private Set<Long> getLikesByFilmId(Long filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId));
    }

    public Film createFilm(Film film) {
        if (film.getMpa() == null || !validRatingIds.contains(film.getMpa().getId())) {
            throw new NotFoundException("Некорректный рейтинг: " + film.getMpa());
        }

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (!validGenreIds.contains(genre.getId())) {
                    throw new NotFoundException("Некорректный жанр: " + genre.getId());
                }
            }
        }
        validateFilm(film);
        return filmStorage.createFilm(film);
    }

    private void loadGenres() {
        String sql = "SELECT id FROM genre";
        validGenreIds.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("id")));
    }

    private void loadRatings() {
        String sql = "SELECT id FROM rating";
        validRatingIds.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("id")));
    }
}