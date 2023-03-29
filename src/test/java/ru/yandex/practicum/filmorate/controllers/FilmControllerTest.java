package ru.yandex.practicum.filmorate.controllers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FilmControllerTest extends ControllerTest {
    @Test
    @SneakyThrows
    public void createFilm_withoutName() {
        Film film = Film.builder()
                .id(1)
                .description("descript")
                .releaseDate(LocalDate.of(1987, 11, 12))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Имя не может быть пустым", violation.getMessage());

        String jsonFilm = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(jsonFilm))
                .andDo(h -> {
                    System.out.println(h.getResponse().getStatus());
                })
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    public void createFilm_withDescriptionOver200Length() {
        Film film = Film.builder()
                .id(1)
                .name("Titanic")
                .description("descript,descript,descript,descript,descript,descript," +
                        "descript,descript,descript,descript,descript,descript,descript,descript," +
                        "descript,descript,descript,descript,descript,descript,descript,descript," +
                        "descript,descript,descript,descript,descript,descript,descript,descript," +
                        "descript,descript,descript,descript,descript,descript,descript,descript," +
                        "descript,descript,descript,descript,descript,descript,descript,descript," +
                        "descript,descript,descript,descript,descript,descript,descript,descript," +
                        "descript,descript,descript,descript,descript,descript,descript,descript," +
                        "descript,descript,descript,descript,descript,descript,")
                .releaseDate(LocalDate.of(1987, 11, 12))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("максимальная длина описания — 200 символов", violation.getMessage());

        String jsonFilm = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(jsonFilm))
                .andDo(h -> {
                    System.out.println(h.getResponse().getStatus());
                })
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void createFilm_Before_28_12_1895() {
        Film film = Film.builder()
                .id(1)
                .name("Titanic")
                .description("descript")
                .releaseDate(LocalDate.of(1894, 11, 12))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("дата релиза — не раньше 28.12.1895", violation.getMessage());

        String jsonFilm = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(jsonFilm))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void createFilm_withNegativeOrZeroDuration() {
        Film film = Film.builder()
                .id(1)
                .name("titan")
                .description("descript")
                .releaseDate(LocalDate.of(1987, 11, 12))
                .duration(-15)
                .build();
        Film film2 = Film.builder()
                .id(2)
                .name("Shrek")
                .description("descript")
                .releaseDate(LocalDate.of(1987, 10, 12))
                .duration(0)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Set<ConstraintViolation<Film>> violations2 = validator.validate(film2);
        ConstraintViolation<Film> violation = violations.iterator().next();
        ConstraintViolation<Film> violation2 = violations2.iterator().next();
        assertEquals("Продолжительность фильма не может быть отрицательной или равной нулю", violation.getMessage());
        assertEquals("Продолжительность фильма не может быть отрицательной или равной нулю", violation2.getMessage());
    }

    @Test
    @SneakyThrows
    public void httpCreateFilm_withNegativeOrZeroDuration() {
        Film film = Film.builder().id(1).name("titan").description("descript")
                .releaseDate(LocalDate.of(1987, 11, 12))
                .duration(-15)
                .build();
        Film film2 = Film.builder().id(2).name("Shrek").description("descript")
                .releaseDate(LocalDate.of(1987, 10, 12))
                .duration(0)
                .build();
        String jsonFilm = objectMapper.writeValueAsString(film);
        String jsonFilm2 = objectMapper.writeValueAsString(film2);
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(jsonFilm))
                .andDo(h -> {
                    System.out.println(h.getResponse().getStatus());
                })
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(jsonFilm2))
                .andDo(h -> {
                    System.out.println(h.getResponse().getStatus());
                })
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void getFilmById_whenPathVariableIsInvalidOrNotExisting() {
        mockMvc.perform(get("/films/-1"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/films/500"))
                .andExpect(status().isNotFound());
    }
}
