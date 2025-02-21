package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.Mocks;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_ShouldCreateUser_WhenValidData() {
        // Arrange
        User user = Mocks.getRandomUser();

        // Act
        User createdUser = userService.createUser(user);

        // Assert
        assertNotNull(createdUser.getId());
        assertEquals(user.getName(), createdUser.getName());
        assertEquals(user.getLogin(), createdUser.getLogin());
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getBirthday(), createdUser.getBirthday());
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailIsEmpty() {
        // Arrange
        User user = Mocks.getRandomUser();
        user.setEmail("");

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> userService.createUser(user));
        assertEquals("[email: не должно быть пустым]", exception.getMessage());
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailIsInvalid() {
        // Arrange
        User user = Mocks.getRandomUser();
        user.setEmail("invalid-email");

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> userService.createUser(user));
        assertEquals("[email: должно иметь формат адреса электронной почты]", exception.getMessage());
    }

    @Test
    void createUser_ShouldThrowException_WhenLoginIsEmpty() {
        // Arrange
        User user = Mocks.getRandomUser();
        user.setLogin("");

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> userService.createUser(user));
        assertEquals("[login: не должно быть пустым]", exception.getMessage());
    }

    @Test
    void createUser_ShouldThrowException_WhenLoginContainsSpaces() {
        // Arrange
        User user = Mocks.getRandomUser();
        user.setLogin("invalid login");

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> userService.createUser(user));
        assertEquals("[login: Login не должен содержать пробелов]", exception.getMessage());
    }

    @Test
    void createUser_ShouldUseLoginAsName_WhenNameIsEmpty() {
        // Arrange
        User user = Mocks.getRandomUser();
        user.setName("");

        // Act
        User createdUser = userService.createUser(user);

        // Assert
        assertEquals(user.getLogin(), createdUser.getName());
    }

    @Test
    void createUser_ShouldThrowException_WhenBirthdayIsInTheFuture() {
        // Arrange
        User user = Mocks.getRandomUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> userService.createUser(user));
        assertEquals("[birthday: дата рождения не может быть в будущем]", exception.getMessage());
    }
}