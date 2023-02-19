package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private final Map<Integer, User> users = new HashMap<>();
    private static final AtomicInteger idCounter = new AtomicInteger(0);

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    private User validate(User user) {
        if (user.getEmail().isBlank()
                || !user.getEmail().matches("\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}")) {
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        } else if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("дата рождения не может быть в будущем");
        }
        return user;
    }

    @PostMapping
    public User create(@RequestBody User user) throws JsonProcessingException {
        User validatedUser;
        try {
            validatedUser = validate(user);
            if (validatedUser.getName() == null || validatedUser.getName().isBlank()) {
                validatedUser.setName(validatedUser.getLogin());
            }
        } catch (ValidationException ex) {
            log.error(ex.getMessage());
            throw new ValidationException(ex.getMessage());
        }
        validatedUser.setId(idCounter.incrementAndGet());
        users.put(validatedUser.getId(), validatedUser);
        log.info("Добавлен пользователь : {}", mapper.writeValueAsString(validatedUser));
        return validatedUser;
    }

    @PutMapping
    public User update(@RequestBody User user) throws JsonProcessingException {
        User validatedUser;
        try {
            validatedUser = validate(user);
        } catch (ValidationException ex) {
            log.error(ex.getMessage());
            throw new ValidationException(ex.getMessage());
        }
        if (users.containsKey(validatedUser.getId())) {
            users.put(validatedUser.getId(), validatedUser);
            log.info("Обновлен пользователь : {}", mapper.writeValueAsString(validatedUser));
            return validatedUser;
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }
}
