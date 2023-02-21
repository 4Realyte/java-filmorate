package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
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
    @PostMapping
    public User create(@Valid @RequestBody User user) throws JsonProcessingException {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(idCounter.incrementAndGet());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь : {}", mapper.writeValueAsString(user));
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) throws JsonProcessingException {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Обновлен пользователь : {}", mapper.writeValueAsString(user));
            return user;
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }
}
