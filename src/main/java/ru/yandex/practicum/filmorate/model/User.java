package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * User
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    Long id;

    @NotBlank(message = "Не должно быть пустым")
    @Email(message = "Некорректный формат email")
    String email;

    @NotBlank(message = "Не должно быть пустым")
    @Pattern(regexp = "\\S+", message = "Login не должен содержать пробелов")
    String login;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;

    String name;

    String status;
}