package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.Mocks;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryFilmStorageTest {

    @InjectMocks
    private InMemoryFilmStorage inMemoryFilmStorage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createFilm_ShouldCreateFilm_WhenValidData() {
        // Arrange
        Film film = Mocks.getRandomFilm();

        // Act
        Film createdFilm = inMemoryFilmStorage.createFilm(film);

        // Assert
        assertNotNull(createdFilm.getId());
        assertEquals(film.getName(), createdFilm.getName());
        assertEquals(film.getDescription(), createdFilm.getDescription());
        assertEquals(film.getReleaseDate(), createdFilm.getReleaseDate());
        assertEquals(film.getDuration(), createdFilm.getDuration());
    }

    @Test
    void updateFilm_ShouldUpdateFilm_WhenValidData() {
        // Arrange
        Film film = Mocks.getRandomFilm();
        Film createdFilm = inMemoryFilmStorage.createFilm(film);

        createdFilm.setName("Updated Name");

        // Act
        Film updatedFilm = inMemoryFilmStorage.updateFilm(createdFilm);

        // Assert
        assertEquals("Updated Name", updatedFilm.getName());
    }

    @Test
    void updateFilm_ShouldThrowException_WhenFilmNotFound() {
        // Arrange
        Film film = Mocks.getRandomFilm();
        film.setId(999L);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> inMemoryFilmStorage.updateFilm(film));
        assertEquals("Фильм с таким id не найден", exception.getMessage());
    }

    @Test
    void isReleaseDateValid_ShouldReturnTrue_WhenDateIsAfterMinDate() {
        // Arrange
        LocalDate validDate = LocalDate.of(2000, Month.JANUARY, 1);

        // Act
        boolean isValid = inMemoryFilmStorage.isReleaseDateValid(validDate);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isReleaseDateValid_ShouldReturnFalse_WhenDateIsBeforeMinDate() {
        // Arrange
        LocalDate invalidDate = LocalDate.of(1890, Month.JANUARY, 1);

        // Act
        boolean isValid = inMemoryFilmStorage.isReleaseDateValid(invalidDate);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void createFilm_ShouldThrowException_WhenReleaseDateIsBefore1895() {
        // Arrange
        Film film = Mocks.getRandomFilm();
        film.setReleaseDate(LocalDate.of(1890, 1, 1));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> inMemoryFilmStorage.createFilm(film));
        assertTrue(exception.getMessage().contains("[releaseDate: Дата релиза не может быть раньше 28 декабря 1895 года]"));
    }

    @Test
    void createFilm_ShouldThrowException_WhenDurationIsZeroOrNegative() {
        // Arrange
        Film film = Mocks.getRandomFilm();
        film.setDuration(Duration.ofMinutes(0));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> inMemoryFilmStorage.createFilm(film));
        assertTrue(exception.getMessage().contains("[duration: Продолжительность фильма должна быть положительным числом]"));
    }
}