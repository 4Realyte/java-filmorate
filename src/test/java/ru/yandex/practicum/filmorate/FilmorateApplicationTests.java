package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {
    ValidatorFactory factory;
    Validator validator;
    FilmController filmController;
    UserController userController;

    @BeforeEach
    void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        filmController = new FilmController();
        userController =  new UserController();
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void createFilm_withoutName() throws IOException, InterruptedException {
        Film film = Film.builder()
                .id(1)
                .description("descript")
                .releaseDate(LocalDate.of(1987, 11, 12))
                .duration(100)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Имя не может быть пустым", violation.getMessage());
    }

    @Test
    public void createFilm_withDescriptionOver200Length() throws IOException, InterruptedException {
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
    }

    @Test
    public void createFilm_Before_28_12_1895() throws IOException, InterruptedException {
        FilmController controller = new FilmController();
        ValidationException ex = assertThrows(ValidationException.class, () -> {
            Film film = Film.builder()
                    .id(1)
                    .name("Titanic")
                    .description("descript")
                    .releaseDate(LocalDate.of(1894, 11, 12))
                    .duration(100)
                    .build();
            filmController.create(film);
        });
        assertEquals("дата релиза — не раньше 28.12.1895", ex.getMessage());
    }
    @Test
    public void createFilm_withNegativeOrZeroDuration() throws IOException, InterruptedException {
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
    public void createUser_withoutEmail() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1)
                .name("Alex")
                .login("Ken")
                .birthday(LocalDate.of(1994,10,23))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Email shouldn't be empty", violation.getMessage());
    }
    @Test
    public void createUser_withInvalidEmail() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1)
                .email("@las.same")
                .name("Alex")
                .login("Ken")
                .birthday(LocalDate.of(1994,10,23))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Email should be valid", violation.getMessage());
    }
    @Test
    public void createUser_emptyLogin() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1)
                .email("yandex@mail.com")
                .name("Alex")
                .login(" ")
                .birthday(LocalDate.of(1994,10,23))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("login shouldn't be empty", violation.getMessage());
    }
    @Test
    public void createUser_shouldUseLoginWhenNameIsEmpty() throws IOException, InterruptedException {
        User user = User.builder()
                .id(1)
                .email("yandex@mail.com")
                .login("Nick")
                .birthday(LocalDate.of(1994,10,23))
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
                .birthday(LocalDate.of(2045,10,23))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("дата рождения не может быть в будущем", violation.getMessage());
    }

}
