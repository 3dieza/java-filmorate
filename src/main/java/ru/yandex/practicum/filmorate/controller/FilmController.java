package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final FilmDbStorage filmDbStorage;

    @PostMapping

    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        Film createdFilm = filmService.createFilm(film);
        return ResponseEntity.ok(createdFilm);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        Film updatedFilm = filmDbStorage.updateFilm(film);
        return ResponseEntity.ok(updatedFilm);
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmDbStorage.getFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Boolean> addLike(@PathVariable Long id, @PathVariable Long userId) {
        boolean isLiked = filmService.addLike(id, userId);
        return ResponseEntity.ok(isLiked);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Boolean> removeLike(@PathVariable Long id, @PathVariable Long userId) {
        boolean isDeleted = filmService.deleteLike(id, userId);
        return ResponseEntity.ok(isDeleted);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> findPopularFilm(@RequestParam(defaultValue = "10") Long count) {
        return ResponseEntity.ok(filmService.findPopularFilm(count));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable Long id) {
        Film film = filmDbStorage.getFilmById(id);
        return ResponseEntity.ok(film);
    }
}