package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(UserDbStorage.class)
@Sql(statements = {
        "DELETE FROM user_friends",
        "DELETE FROM film_likes",
        "DELETE FROM film_genre",
        "DELETE FROM film",
        "DELETE FROM users"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserServiceTest {

    @Autowired
    private UserDbStorage userDbStorage;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User(null, "user1@mail.com", "user1",
                LocalDate.of(1990, 1, 1), "User One", null);
        user2 = new User(null, "user2@mail.com", "user2",
                LocalDate.of(1991, 2, 2), "User Two", null);
    }

    @Test
    void shouldCreateAndFindUserById() {
        User created = userDbStorage.createUser(user1);
        User found = userDbStorage.findById(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getEmail()).isEqualTo(user1.getEmail());
    }

    @Test
    void shouldUpdateUser() {
        User created = userDbStorage.createUser(user1);
        created.setName("Updated Name");
        User updated = userDbStorage.updateUser(created);

        assertThat(updated.getName()).isEqualTo("Updated Name");
    }

    @Test
    void shouldAddAndGetFriends() {
        User u1 = userDbStorage.createUser(user1);
        User u2 = userDbStorage.createUser(user2);

        userDbStorage.addNewFriend(u1.getId(), u2.getId());

        Set<Long> friends = userDbStorage.getFriends(u1.getId());
        assertThat(friends).containsExactly(u2.getId());
    }

    @Test
    void shouldDeleteFriend() {
        User u1 = userDbStorage.createUser(user1);
        User u2 = userDbStorage.createUser(user2);

        userDbStorage.addNewFriend(u1.getId(), u2.getId());
        userDbStorage.deleteFriend(u1.getId(), u2.getId());

        Set<Long> friends = userDbStorage.getFriends(u1.getId());
        assertThat(friends).isEmpty();
    }
}