package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import({UserDbStorage.class, FilmDbStorage.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class FilmServiceTest {

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserDbStorage userStorage;

    private Film film;
    private User user;


    @BeforeEach
    void setup(@Autowired JdbcTemplate jdbcTemplate) {
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM film_genre");
        jdbcTemplate.update("DELETE FROM film");
        jdbcTemplate.update("DELETE FROM user_friends");
        jdbcTemplate.update("DELETE FROM users");

        film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(Duration.ofMinutes(120));
        film.setMpa(new Rating(1, null));

        Genre genre = new Genre();
        genre.setId(1);
        film.setGenres(List.of(genre));

        user = userStorage.createUser(new User(null, "test+" + System.nanoTime() + "@mail.com", "testUser_" + System.nanoTime(),
                LocalDate.of(1980, 1, 1), "Test User", null)
        );
        film = filmService.createFilm(film);
    }

    @Test
    void shouldCreateFilm() {
        assertThat(film.getId()).isNotNull();
        assertThat(film.getName()).isEqualTo("Test Film");
        assertThat(film.getGenres()).isNotEmpty();
        assertThat(film.getMpa()).isNotNull();
    }

    @Test
    void shouldAddLikeToFilm() {
        boolean result = filmService.addLike(film.getId(), user.getId());

        assertTrue(result);
        Set<Long> likes = filmService.getLikes(film.getId());
        assertThat(likes).contains(user.getId());
    }

    @Test
    void shouldDeleteLikeFromFilm() {
        filmService.addLike(film.getId(), user.getId());

        boolean removed = filmService.deleteLike(film.getId(), user.getId());

        assertTrue(removed);
        assertThat(filmService.getLikes(film.getId())).doesNotContain(user.getId());
    }

    @Test
    void shouldThrowExceptionWhenAddingDuplicateLike() {
        filmService.addLike(film.getId(), user.getId());

        var ex = assertThrows(Exception.class,
                () -> filmService.addLike(film.getId(), user.getId()));

        assertTrue(ex.getMessage().contains("Пользователь уже ставил лайк"));
    }

    @Test
    void shouldFindPopularFilmsSortedByLikes() {
        Film second = new Film();
        second.setName("Второй фильм");
        second.setDescription("Еще один");
        second.setReleaseDate(LocalDate.of(2010, 1, 1));
        second.setDuration(Duration.ofMinutes(90));
        second.setMpa(new Rating(1, null));

        Film secondFilm = filmService.createFilm(second);

        User anotherUser = userStorage.createUser(
                new User(null, "other@ya.ru", "other",
                        LocalDate.of(1995, 5, 5), "Другой", "active"));

        filmService.addLike(film.getId(), user.getId());              // 1 лайк
        filmService.addLike(secondFilm.getId(), anotherUser.getId()); // 1 лайк
        filmService.addLike(film.getId(), anotherUser.getId());       // 2 лайка

        var popular = filmService.findPopularFilm(2L);

        assertEquals(2, popular.size());
        assertTrue(popular.get(0).getLikes().size() >= popular.get(1).getLikes().size());

        assertEquals(2, popular.get(0).getLikes().size());
    }
}