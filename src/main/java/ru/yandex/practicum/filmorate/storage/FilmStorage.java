package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    Collection<Film> getFilms();

    boolean isReleaseDateValid(LocalDate releaseDate);

    Film getFilmById(Long id);
}