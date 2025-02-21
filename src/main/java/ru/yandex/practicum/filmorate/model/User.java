package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * User.
 */
@Data
public class User {
    private Long id;

    private String email;

    private String login;

    private LocalDate birthday;

    private String name;
}