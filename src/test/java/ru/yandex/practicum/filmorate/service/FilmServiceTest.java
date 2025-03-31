package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmServiceTest {

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserDbStorage userStorage;

    private Film film;
    private User user;

    @BeforeEach
    void setup() {
        user = userStorage.createUser(new User(null, "like@ya.ru", "liker",
                LocalDate.of(1990, 1, 1), "Лайкнутый", "active"));

        film = new Film();
        film.setName("Лайкаемый фильм");
        film.setDescription("Фильм для теста лайков");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(Duration.ofMinutes(100));
        film.setMpa(new Rating(1, null));

        film = filmService.createFilm(film);
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