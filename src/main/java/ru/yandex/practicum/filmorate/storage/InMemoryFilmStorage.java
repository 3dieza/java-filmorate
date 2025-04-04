package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.util.Validator.validateFilm;

@Slf4j
@Component
@Qualifier("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long currentMaxId = 0;

    private long getNextId() {
        return ++currentMaxId;
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        if (film.getId() == null || film.getId() == 0) {
            film.setId(getNextId());
        }
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм с id: {}", film.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null || !films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с таким id не найден");
        }
        validateFilm(film);
        films.put(film.getId(), film);
        log.info("Фильм обновлен с id: {}", film.getId());
        return film;
    }

    public Collection<Film> getFilms() {
        return films.values();
    }

    public boolean isReleaseDateValid(LocalDate releaseDate) {
        LocalDate minDate = LocalDate.of(1895, Month.DECEMBER, 28);
        return !releaseDate.isBefore(minDate);
    }

    @Override
    public Film getFilmById(Long id) {
        return films.get(id);
    }

}