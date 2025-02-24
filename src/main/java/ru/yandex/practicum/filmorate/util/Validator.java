package ru.yandex.practicum.filmorate.util;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;

@Slf4j
public class Validator {
    public static void validateFilm(Film film) {
        validateDuration(film.getDuration());
        validateReleaseDate(film.getReleaseDate());
    }

    private static void validateDuration(Duration duration) {
        if (duration == null || duration.isZero() || duration.isNegative()) {
            throw new ValidationException("[duration: Продолжительность фильма должна быть положительным числом]");
        }
    }

    private static void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate != null && releaseDate.isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            throw new ValidationException("[releaseDate: Дата релиза не может быть раньше 28 декабря 1895 года]");
        }
    }
}