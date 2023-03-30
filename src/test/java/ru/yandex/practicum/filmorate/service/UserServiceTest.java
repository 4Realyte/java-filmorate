package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
public class UserServiceTest extends ServiceTest {
    @Test
    public void getUsers_shouldReturnCorrectSize() {
        assertEquals(0, userService.getUsers().size());
        userService.createUser(User.builder()
                .id(1)
                .email("alex@mail.com")
                .name("Alex")
                .login("Ken")
                .birthday(LocalDate.of(1994, 10, 23))
                .build());
        assertEquals(1, userService.getUsers().size());
    }

    @Test
    public void createUser_shouldCreateUser() {
        User user = userService.createUser(User.builder()
                .id(1)
                .email("alex@mail.com")
                .name("Alex")
                .login("Ken")
                .birthday(LocalDate.of(1994, 10, 23))
                .build());
        assertNotNull(user);
    }

    @Test
    public void updateUser_shouldThrowUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(User.builder()
                .id(1)
                .email("alex@mail.com")
                .name("Alex")
                .login("Ken")
                .birthday(LocalDate.of(1994, 10, 23))
                .build()));
    }

    @Test
    public void updateUser_shouldUpdateUser() {
        User user = userService.createUser(User.builder()
                .id(1)
                .email("alex@mail.com")
                .name("Alex")
                .login("Ken")
                .birthday(LocalDate.of(1994, 10, 23))
                .build());
        user.setName("John");
        User updated = userService.updateUser(user);
        assertEquals("John", updated.getName());
    }

    @Test
    public void getUserById_shouldThrowUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1));
        userService.createUser(User.builder()
                .email("alex@mail.com")
                .name("Alex")
                .login("Ken")
                .birthday(LocalDate.of(1994, 10, 23))
                .build());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(2));
    }

    @Test
    public void getUserById_shouldReturnPreviouslyAddedUser() {
        User user = userService.createUser(User.builder()
                .email("alex@mail.com")
                .name("Alex")
                .login("Ken")
                .birthday(LocalDate.of(1994, 10, 23))
                .build());
        assertEquals(3, userService.getUserById(3).getId());
    }
}
