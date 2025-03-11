package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final InMemoryUserStorage inMemoryUserStorage;
    private final UserService userService;

    public UserController(InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok(inMemoryUserStorage.createUser(user));
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok(inMemoryUserStorage.updateUser(user));
    }

    @GetMapping
    public Collection<User> getUsers() {
        return inMemoryUserStorage.getUsers();
    }

    @PutMapping("/{id}/friends/{friend_id}")
    public ResponseEntity<User> addFriend(@PathVariable Long id, @PathVariable("friend_id") Long friendId) {
        User updatedUser = userService.addFriend(id, friendId);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Set<Map<String, Long>>> getFriends(@PathVariable Long id) {
        Set<Long> friendIds = userService.getFriends(id);

        Set<Map<String, Long>> response = friendIds.stream()
                .map(friendId -> Collections.singletonMap("id", friendId))
                .collect(Collectors.toSet());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/friends/{friend_id}")
    public void deleteFriend(@PathVariable Long id, @PathVariable("friend_id") Long friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<Set<Map<String, Long>>> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        Set<Long> friendIds = userService.findCommonFriends(id, otherId);

        Set<Map<String, Long>> response = friendIds.stream()
                .map(friendId -> Collections.singletonMap("id", friendId))
                .collect(Collectors.toSet());

        return ResponseEntity.ok(response);
    }
}