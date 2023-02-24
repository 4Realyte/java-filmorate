package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmorateApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private ValidatorFactory factory;
    private Validator validator;
    private FilmController filmController;
    private UserController userController;

    @BeforeEach
    void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        filmController = new FilmController();
        userController = new UserController();
    }

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
        FilmController controller = new FilmController();
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
        ValidationException ex = assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
        assertEquals("дата релиза — не раньше 28.12.1895", ex.getMessage());
    }

    @Test
    @SneakyThrows
    public void createFilm_withNegativeOrZeroDuration() {
        FilmController controller = new FilmController();
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
    public void HttpCreateFilm_withNegativeOrZeroDuration() {
        FilmController controller = new FilmController();
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
    public void createUser_withoutEmail() {
        User user = User.builder()
                .id(1)
                .name("Alex")
                .login("Ken")
                .birthday(LocalDate.of(1994, 10, 23))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Электронная почта не может быть пустой", violation.getMessage());

        String jsonUser = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(jsonUser))
                .andDo(h -> {
                    System.out.println(h.getResponse().getStatus());
                })
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUser_withInvalidEmail() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1)
                .email("@las.same")
                .name("Alex")
                .login("Ken")
                .birthday(LocalDate.of(1994, 10, 23))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Эмейл должен быть корректным", violation.getMessage());
    }

    @Test
    public void createUser_emptyLogin() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1)
                .email("yandex@mail.com")
                .name("Alex")
                .login(" ")
                .birthday(LocalDate.of(1994, 10, 23))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Логин не может быть пустым", violation.getMessage());
    }

    @Test
    public void createUser_shouldUseLoginWhenNameIsEmpty() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1)
                .email("yandex@mail.com")
                .login("Nick")
                .birthday(LocalDate.of(1994, 10, 23))
                .build();
        User createdUser = userController.create(user);
        assertEquals(user.getLogin(), createdUser.getName());
    }

    @Test
    public void createUser_withFutureBirthDate() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1)
                .email("yandex@mail.com")
                .name("Marty")
                .login("McFly")
                .birthday(LocalDate.of(2045, 10, 23))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("дата рождения не может быть в будущем", violation.getMessage());
    }

    @Test
    @SneakyThrows
    public void getUsers_shouldReturnEmptyList() {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }

}
