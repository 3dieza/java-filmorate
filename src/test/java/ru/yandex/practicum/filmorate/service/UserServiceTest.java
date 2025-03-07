package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.Mocks;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    void createUser_ShouldUseLoginAsName_WhenNameIsEmpty() {
        // Arrange
        User user = Mocks.getRandomUser();
        user.setName("");

        // Act
        User createdUser = userService.createUser(user);

        // Assert
        assertEquals(user.getLogin(), createdUser.getName());
    }
}