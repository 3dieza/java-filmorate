package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.util.DurationDeserializer;
import ru.yandex.practicum.filmorate.util.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FilmController.class)
@Disabled
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Регистрируем сериализаторы и десериализаторы
        SimpleModule module = new SimpleModule();
        module.addSerializer(Duration.class, new DurationSerializer());
        module.addDeserializer(Duration.class, new DurationDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    void createFilm_ShouldReturnFilm_WhenValidRequest() throws Exception {
        // Arrange
        Film film = new Film();
        film.setId(1L);
        film.setName("Film Name");
        film.setDescription("Film Description");
        film.setReleaseDate(LocalDate.of(2000, Month.JANUARY, 1));
        film.setDuration(Duration.ofMinutes(120));

        when(inMemoryFilmStorage.createFilm(film)).thenReturn(film);

        // Act & Assert
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Film Name\",\"description\":\"Film Description\",\"releaseDate\":\"2000-01-01\",\"duration\":120}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Film Name"))
                .andExpect(jsonPath("$.description").value("Film Description"))
                .andExpect(jsonPath("$.releaseDate").value("2000-01-01"))
                .andExpect(jsonPath("$.duration").value(120));
    }

    @Test
    void updateFilm_ShouldReturnOk_WhenValidRequest() throws Exception {
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Updated Film\",\"description\":\"Updated Description\",\"releaseDate\":\"2000-01-01\",\"duration\":120}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateFilm_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"\",\"description\":\"Description\",\"releaseDate\":\"2000-01-01\",\"duration\":120}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFilms_ShouldReturnListOfFilms() throws Exception {
        // Arrange
        Film film1 = new Film();
        film1.setId(1L);
        film1.setName("Film One");
        film1.setDescription("Description One");
        film1.setReleaseDate(LocalDate.of(2000, Month.JANUARY, 1));
        film1.setDuration(Duration.ofMinutes(120));

        when(inMemoryFilmStorage.getFilms()).thenReturn(List.of(film1));

        // Act & Assert
        mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Film One"))
                .andExpect(jsonPath("$[0].description").value("Description One"))
                .andExpect(jsonPath("$[0].releaseDate").value("2000-01-01"))
                .andExpect(jsonPath("$[0].duration").value(120));
    }
}