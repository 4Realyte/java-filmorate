package ru.yandex.practicum.filmorate.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private final Map<Integer, User> users = new HashMap<>();
    private static final AtomicInteger idCounter = new AtomicInteger(0);


    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    public User getUserById(Integer id) {
        if (!users.containsKey(id))
            throw new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", id));
        return users.get(id);
    }

    @SneakyThrows
    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(idCounter.incrementAndGet());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь : {}", mapper.writeValueAsString(user));
        return user;
    }

    @SneakyThrows
    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Обновлен пользователь : {}", mapper.writeValueAsString(user));
            return user;
        } else {
            throw new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", user.getId()));
        }
    }
}
