package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(UserDbStorage.class)
@ActiveProfiles("test")
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void createUser_shouldReturnUserWithId() {
        User user = new User(
                null,
                "test@example.com",
                "testlogin",
                LocalDate.of(1990, 1, 1),
                "Test User",
                "active"
        );

        User created = userStorage.createUser(user);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getEmail()).isEqualTo("test@example.com");
        assertThat(created.getStatus()).isEqualTo("active");
    }

    @Test
    void findById_shouldReturnUserIfExists() {
        User user = new User(null, "find@example.com", "findme",
                LocalDate.of(1991, 2, 2), "Find Me", "active");
        User created = userStorage.createUser(user);

        User found = userStorage.findById(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getLogin()).isEqualTo("findme");
    }

    @Test
    void updateUser_shouldUpdateFieldsCorrectly() {
        User user = new User(null, "old@example.com", "oldlogin",
                LocalDate.of(1985, 5, 5), "Old Name", "inactive");
        User created = userStorage.createUser(user);

        created.setEmail("new@example.com");
        created.setName("New Name");
        created.setStatus("active");

        userStorage.updateUser(created);
        User updated = userStorage.findById(created.getId());

        assertThat(updated.getEmail()).isEqualTo("new@example.com");
        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getStatus()).isEqualTo("active");
    }

    @Test
    void getUsers_shouldReturnAllUsers() {
        // Очищаем таблицу
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM user_friends");
        jdbcTemplate.update("DELETE FROM users");

        userStorage.createUser(new User(null, "a@a.com", "a",
                LocalDate.of(1990, 1, 1), "A", "active"));
        userStorage.createUser(new User(null, "b@b.com", "b",
                LocalDate.of(1992, 2, 2), "B", "inactive"));

        assertThat(userStorage.getUsers()).hasSize(2);
    }

    @Test
    void getFriends_shouldReturnEmptySetByDefault() {
        User user = userStorage.createUser(new User(null, "no@friends.com", "nofriends",
                LocalDate.of(1999, 3, 3), "No Friends", "ghost"));

        assertThat(userStorage.getFriends(user.getId())).isEmpty();
    }
}