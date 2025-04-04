package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(FilmDbStorage.class)
@ActiveProfiles("test")
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    private Film testFilm;

    @BeforeEach
    void setup() {
        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Description");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(Duration.ofMinutes(120));
        testFilm.setMpa(new Rating(1, null));
        Genre genre = new Genre();
        genre.setId(1);
        testFilm.setGenres(List.of(genre));
    }

    @Test
    void shouldCreateFilm() {
        Film created = filmStorage.createFilm(testFilm);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Test Film");
        assertThat(created.getGenres()).isNotEmpty();
        assertThat(created.getMpa()).isNotNull();
    }

    @Test
    void shouldUpdateFilm() {
        Film created = filmStorage.createFilm(testFilm);
        created.setName("Updated Name");

        Film updated = filmStorage.updateFilm(created);

        assertThat(updated.getName()).isEqualTo("Updated Name");
    }

    @Test
    void shouldGetFilmById() {
        Film created = filmStorage.createFilm(testFilm);

        Film found = filmStorage.getFilmById(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
    }

    @Test
    void shouldGetAllFilms() {
        filmStorage.createFilm(testFilm);

        assertThat(filmStorage.getFilms()).isNotEmpty();
    }

    @Test
    void shouldCheckValidReleaseDate() {
        assertThat(filmStorage.isReleaseDateValid(LocalDate.of(2000, 1, 1))).isTrue();
        assertThat(filmStorage.isReleaseDateValid(LocalDate.of(1800, 1, 1))).isFalse();
    }

    @Test
    void shouldGetGenresForFilm() {
        Film created = filmStorage.createFilm(testFilm);

        List<Genre> genres = filmStorage.getGenres(created.getId());

        assertThat(genres).isNotEmpty();
    }
}
