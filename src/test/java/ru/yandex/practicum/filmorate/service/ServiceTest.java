package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

abstract class ServiceTest {
    protected FilmService filmService;
    protected UserService userService;

    @BeforeEach
    public void init() {
        UserStorage userStorage = new InMemoryUserStorage();
        filmService = new FilmService(new InMemoryFilmStorage(), userStorage);
        userService = new UserService(userStorage);
    }
}
