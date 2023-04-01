package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.impl.FilmLikeDaoImpl;
import ru.yandex.practicum.filmorate.storage.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.storage.dao.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryUserStorage;

@AutoConfigureTestDatabase
@RequiredArgsConstructor
abstract class ServiceTest {
    protected FilmService filmService;
    protected UserService userService;
    protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void init() {
        UserStorage userStorage = new InMemoryUserStorage();
        filmService = new FilmService(new InMemoryFilmStorage(), userStorage,
                new FilmLikeDaoImpl(jdbcTemplate), new GenreDaoImpl(jdbcTemplate), new MpaDaoImpl(jdbcTemplate));
        userService = new UserService(userStorage);
    }
}
