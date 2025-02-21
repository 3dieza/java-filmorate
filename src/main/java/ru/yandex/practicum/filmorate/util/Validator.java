package ru.yandex.practicum.filmorate.util;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

@Slf4j
public class Validator {
    public static void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            log.error("Email не может быть пустым.");
            throw new ValidationException("[email: не должно быть пустым]");
        }
        if (!user.getEmail().contains("@")) {
            log.error("Некорректный формат email: {}", user.getEmail());
            throw new ValidationException("[email: должно иметь формат адреса электронной почты]");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty()) {
            log.error("Login не может быть пустым.");
            throw new ValidationException("[login: не должно быть пустым]");
        }
        if (user.getLogin().contains(" ")) {
            log.error("Login не может содержать пробелов.");
            throw new ValidationException("[login: Login не должен содержать пробелов]");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем.");
            throw new ValidationException("[birthday: дата рождения не может быть в будущем]");
        }
    }

    public static void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            log.error("Название фильма не может быть пустым.");
            throw new ValidationException("[name: не должно быть пустым]");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Максимальная длина описания — 200 символов.");
            throw new ValidationException("[description: Максимальная длина описания — 200 символов]");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.error("Дата релиза фильма не может быть раньше 28 декабря 1895 года.");
            throw new ValidationException("[releaseDate: Дата релиза не может быть раньше 28 декабря 1895 года]");
        }
        if (film.getDuration() != null && film.getDuration().toMinutes() <= 0) {
            log.error("Продолжительность фильма должна быть положительным числом.");
            throw new ValidationException("[duration: Продолжительность фильма должна быть положительным числом]");
        }
    }
}
