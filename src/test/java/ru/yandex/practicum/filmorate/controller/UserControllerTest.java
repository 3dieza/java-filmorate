//package ru.yandex.practicum.filmorate.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(SpringExtension.class)
//@WebMvcTest(UserController.class)
//@Disabled
//class UserControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private InMemoryUserStorage inMemoryUserStorage;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void createUser_ShouldReturnUser_WhenValidRequest() throws Exception {
//        // Arrange
//        User user = new User();
//        user.setId(1L);
//        user.setEmail("test@example.com");
//        user.setLogin("testUser");
//        user.setBirthday(LocalDate.of(1990, 1, 1));
//        user.setName("Test User");
//
//        when(inMemoryUserStorage.createUser(user)).thenReturn(user);
//
//        // Act & Assert
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.email").value("test@example.com"))
//                .andExpect(jsonPath("$.login").value("testUser"))
//                .andExpect(jsonPath("$.name").value("Test User"));
//    }
//
//    @Test
//    void createUser_ShouldReturnBadRequest_WhenEmailIsEmpty() throws Exception {
//        // Arrange
//        User user = new User();
//        user.setId(1L);
//        user.setEmail(""); // Пустой email
//        user.setLogin("testUser");
//        user.setBirthday(LocalDate.of(1990, 1, 1));
//        user.setName("Test User");
//
//        // Act & Assert
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.code").value("400"))
//                .andExpect(jsonPath("$.message").value("[email: Не должно быть пустым]"));
//    }
//
//    @Test
//    void updateUser_ShouldReturnUser_WhenValidRequest() throws Exception {
//        // Arrange
//        User user = new User();
//        user.setId(1L);
//        user.setEmail("updated@example.com");
//        user.setLogin("updatedUser");
//        user.setBirthday(LocalDate.of(1990, 1, 1));
//        user.setName("Updated User");
//
//        when(inMemoryUserStorage.updateUser(user)).thenReturn(user);
//
//        // Act & Assert
//        mockMvc.perform(put("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.email").value("updated@example.com"))
//                .andExpect(jsonPath("$.login").value("updatedUser"))
//                .andExpect(jsonPath("$.name").value("Updated User"));
//    }
//
//    @Test
//    void getUsers_ShouldReturnListOfUsers() throws Exception {
//        // Arrange
//        User user1 = new User();
//        user1.setId(1L);
//        user1.setEmail("user1@example.com");
//        user1.setLogin("user1");
//        user1.setBirthday(LocalDate.of(1990, 1, 1));
//        user1.setName("User One");
//
//        User user2 = new User();
//        user2.setId(2L);
//        user2.setEmail("user2@example.com");
//        user2.setLogin("user2");
//        user2.setBirthday(LocalDate.of(1992, 2, 2));
//        user2.setName("User Two");
//
//        when(inMemoryUserStorage.getUsers()).thenReturn(List.of(user1, user2));
//
//        // Act & Assert
//        mockMvc.perform(get("/users")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[0].email").value("user1@example.com"))
//                .andExpect(jsonPath("$[0].login").value("user1"))
//                .andExpect(jsonPath("$[0].name").value("User One"))
//                .andExpect(jsonPath("$[1].id").value(2))
//                .andExpect(jsonPath("$[1].email").value("user2@example.com"))
//                .andExpect(jsonPath("$[1].login").value("user2"))
//                .andExpect(jsonPath("$[1].name").value("User Two"));
//    }
//}