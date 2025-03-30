//package ru.yandex.practicum.filmorate.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import ru.yandex.practicum.filmorate.exception.NotFoundException;
//import ru.yandex.practicum.filmorate.exception.ValidationException;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.storage.FilmStorage;
//import ru.yandex.practicum.filmorate.storage.UserStorage;
//
//import java.util.List;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class FilmServiceTest {
//
//    @InjectMocks
//    private FilmService filmService;
//
//    @Mock
//    private UserStorage userStorage;
//
//    @Mock
//    private FilmStorage filmStorage;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void addLike_ShouldAddLike_WhenValidUserAndFilm() {
//        // Arrange
//        Long filmId = 1L;
//        Long userId = 2L;
//        Film film = new Film();
//        film.setId(filmId);
//        User user = new User();
//        user.setId(userId);
//
//        when(filmStorage.getFilmById(filmId)).thenReturn(film);
//        when(userStorage.findById(userId)).thenReturn(user);
//
//        // Act
//        boolean result = filmService.addLike(filmId, userId);
//        Set<Long> likes = film.getLikes();
//
//        // Assert
//        assertTrue(result);
//        assertTrue(likes.contains(userId));
//        verify(filmStorage, times(1)).getFilmById(filmId);
//        verify(userStorage, times(1)).findById(userId);
//    }
//
//    @Test
//    void addLike_ShouldThrowNotFoundException_WhenFilmNotFound() {
//        // Arrange
//        Long filmId = 1L;
//        Long userId = 2L;
//
//        when(filmStorage.getFilmById(filmId)).thenReturn(null);
//
//        // Act & Assert
//        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmService.addLike(filmId, userId));
//        assertEquals("Film or user not found", exception.getMessage());
//    }
//
//    @Test
//    void addLike_ShouldThrowNotFoundException_WhenUserNotFound() {
//        // Arrange
//        Long filmId = 1L;
//        Long userId = 2L;
//        Film film = new Film();
//        film.setId(filmId);
//
//        when(filmStorage.getFilmById(filmId)).thenReturn(film);
//        when(userStorage.findById(userId)).thenReturn(null);
//
//        // Act & Assert
//        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmService.addLike(filmId, userId));
//        assertEquals("Film or user not found", exception.getMessage());
//    }
//
//    @Test
//    void deleteLike_ShouldRemoveLike_WhenLikeExists() {
//        // Arrange
//        Long filmId = 1L;
//        Long userId = 2L;
//        Film film = new Film();
//        film.setId(filmId);
//        User user = new User();
//        user.setId(userId);
//
//        when(filmStorage.getFilmById(filmId)).thenReturn(film);
//        when(userStorage.findById(userId)).thenReturn(user);
//
//        filmService.addLike(filmId, userId);
//        // Act
//        boolean result = filmService.deleteLike(filmId, userId);
//
//        // Assert
//        assertTrue(result);
//        assertTrue(filmService.getLikes(filmId).isEmpty());
//    }
//
//    @Test
//    void deleteLike_ShouldThrowNotFoundException_WhenFilmNotFound() {
//        // Arrange
//        Long filmId = 1L;
//        Long userId = 2L;
//
//        when(filmStorage.getFilmById(filmId)).thenReturn(null);
//
//        // Act & Assert
//        NotFoundException exception = assertThrows(NotFoundException.class, () -> filmService.deleteLike(filmId, userId));
//        assertEquals("Нет фильма или юзера", exception.getMessage());
//    }
//
//    @Test
//    void deleteLike_ShouldThrowValidationException_WhenNoLikeExists() {
//        // Arrange
//        Long filmId = 1L;
//        Long userId = 2L;
//        Film film = new Film();
//        film.setId(filmId);
//        User user = new User();
//        user.setId(userId);
//
//        when(filmStorage.getFilmById(filmId)).thenReturn(film);
//        when(userStorage.findById(userId)).thenReturn(user);
//
//        // Act & Assert
//        ValidationException exception = assertThrows(ValidationException.class, () -> filmService.deleteLike(filmId, userId));
//        assertEquals("У фильма нет лайков", exception.getMessage());
//    }
//
//    @Test
//    void getLikes_ShouldReturnLikes_WhenLikesExist() {
//        // Arrange
//        Long filmId = 1L;
//        Long userId = 2L;
//        Film film = new Film();
//        film.setId(filmId);
//        User user = new User();
//        user.setId(userId);
//
//        when(filmStorage.getFilmById(filmId)).thenReturn(film);
//        when(userStorage.findById(userId)).thenReturn(user);
//
//        filmService.addLike(filmId, userId);
//
//        // Act
//        Set<Long> likes = filmService.getLikes(filmId);
//
//        // Assert
//        assertNotNull(likes);
//        assertTrue(likes.contains(userId));
//    }
//
//    @Test
//    void findPopularFilm_ShouldReturnFilmsSortedByLikes() {
//        // Arrange
//        Film film1 = new Film();
//        film1.setId(1L);
//        Film film2 = new Film();
//        film2.setId(2L);
//        Film film3 = new Film();
//        film3.setId(3L);
//
//        when(filmStorage.getFilms()).thenReturn(List.of(film1, film2, film3));
//
//        when(filmStorage.getFilmById(1L)).thenReturn(film1);
//        when(filmStorage.getFilmById(2L)).thenReturn(film2);
//
//        when(userStorage.findById(100L)).thenReturn(new User());
//        when(userStorage.findById(101L)).thenReturn(new User());
//        when(userStorage.findById(102L)).thenReturn(new User());
//
//        filmService.addLike(1L, 100L);
//        filmService.addLike(1L, 101L);
//        filmService.addLike(2L, 102L);
//
//        // Act
//        List<Film> popularFilms = filmService.findPopularFilm(2L);
//
//        // Assert
//        assertEquals(2, popularFilms.size());
//        assertEquals(1L, popularFilms.get(0).getId());
//        assertEquals(2L, popularFilms.get(1).getId());
//    }
//
//    @Test
//    void findPopularFilm_ShouldReturnTop10_WhenCountIsZeroOrNegative() {
//        // Arrange
//        Film film1 = new Film();
//        film1.setId(1L);
//        Film film2 = new Film();
//        film2.setId(2L);
//
//        when(filmStorage.getFilms()).thenReturn(List.of(film1, film2));
//
//        // Act
//        List<Film> popularFilms = filmService.findPopularFilm(0L);
//
//        // Assert
//        assertEquals(2, popularFilms.size()); // Все фильмы, потому что их всего 2
//    }
//}