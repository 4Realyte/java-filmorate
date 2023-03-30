package ru.yandex.practicum.filmorate.controllers;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureTestDatabase
public class UserControllerTest extends ControllerTest {
    @Autowired
    UserController userController;

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
        assertEquals("Email должен быть корректным", violation.getMessage());
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
                .login("Sam")
                .birthday(LocalDate.of(1994, 10, 23))
                .build();
        User createdUser = userController.create(user);
        assertEquals(user.getLogin(), createdUser.getName());
    }

    @Test
    @SneakyThrows
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
}
