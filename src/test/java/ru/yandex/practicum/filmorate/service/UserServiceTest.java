package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private InMemoryUserStorage userStorage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addFriend_ShouldAddFriend_WhenUsersExist() {
        // Arrange
        Long userId = 1L;
        Long friendId = 2L;
        User user = new User();
        user.setId(userId);
        User friend = new User();
        friend.setId(friendId);

        when(userStorage.findById(userId)).thenReturn(user);
        when(userStorage.findById(friendId)).thenReturn(friend);

        // Act
        User updatedUser = userService.addFriend(userId, friendId);

        // Assert
        assertEquals(userId, updatedUser.getId());
        verify(userStorage, times(1)).addNewFriend(userId, friendId);
    }

    @Test
    void addFriend_ShouldThrowNotFoundException_WhenUserNotFound() {
        // Arrange
        Long userId = 1L;
        Long friendId = 2L;

        when(userStorage.findById(userId)).thenReturn(null);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.addFriend(userId, friendId));
        assertEquals("Один из пользователей не найден", exception.getMessage());
    }

    @Test
    void addFriend_ShouldThrowValidationException_WhenAddingSelfAsFriend() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userStorage.findById(userId)).thenReturn(user);

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> userService.addFriend(userId, userId));
        assertEquals("Нельзя добавить самого себя", exception.getMessage());
    }

    @Test
    void getFriends_ShouldReturnFriendsList_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        Set<Long> friends = new HashSet<>(Set.of(2L, 3L));
        User user = new User();
        user.setId(userId);

        when(userStorage.findById(userId)).thenReturn(user);
        when(userStorage.getFriends(userId)).thenReturn(friends);

        // Act
        Set<Long> result = userService.getFriends(userId);

        // Assert
        assertEquals(friends, result);
    }

    @Test
    void getFriends_ShouldThrowNotFoundException_WhenUserNotFound() {
        // Arrange
        Long userId = 1L;

        when(userStorage.findById(userId)).thenReturn(null);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getFriends(userId));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void deleteFriend_ShouldRemoveFriend_WhenBothUsersExist() {
        // Arrange
        Long userId = 1L;
        Long friendId = 2L;
        User user = new User();
        user.setId(userId);
        User friend = new User();
        friend.setId(friendId);

        when(userStorage.findById(userId)).thenReturn(user);
        when(userStorage.findById(friendId)).thenReturn(friend);

        // Act
        userService.deleteFriend(userId, friendId);

        // Assert
        verify(userStorage, times(1)).getFriends(userId);
        verify(userStorage, times(1)).getFriends(friendId);
    }

    @Test
    void deleteFriend_ShouldThrowNotFoundException_WhenUserNotFound() {
        // Arrange
        Long userId = 1L;
        Long friendId = 2L;

        when(userStorage.findById(userId)).thenReturn(null);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.deleteFriend(userId, friendId));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void findCommonFriends_ShouldReturnCommonFriends_WhenUsersExist() {
        // Arrange
        Long userId = 1L;
        Long friendId = 2L;
        Set<Long> userFriends = new HashSet<>(Set.of(3L, 4L, 5L));
        Set<Long> friendFriends = new HashSet<>(Set.of(4L, 5L, 6L));

        User user = new User();
        user.setId(userId);
        User friend = new User();
        friend.setId(friendId);

        when(userStorage.findById(userId)).thenReturn(user);
        when(userStorage.findById(friendId)).thenReturn(friend);
        when(userStorage.getFriends(userId)).thenReturn(userFriends);
        when(userStorage.getFriends(friendId)).thenReturn(friendFriends);

        // Act
        Set<Long> commonFriends = userService.findCommonFriends(userId, friendId);

        // Assert
        assertEquals(Set.of(4L, 5L), commonFriends);
    }

    @Test
    void findCommonFriends_ShouldThrowNotFoundException_WhenUserNotFound() {
        // Arrange
        Long userId = 1L;
        Long friendId = 2L;

        when(userStorage.findById(userId)).thenReturn(null);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.findCommonFriends(userId, friendId));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userStorage.findById(userId)).thenReturn(user);

        // Act
        User result = userService.getUserById(userId);

        // Assert
        assertEquals(user, result);
    }

    @Test
    void getUserById_ShouldThrowNotFoundException_WhenUserNotFound() {
        // Arrange
        Long userId = 1L;

        when(userStorage.findById(userId)).thenReturn(null);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
        assertEquals("Пользователь не найден", exception.getMessage());
    }
}