package ru.yandex.practicum.filmorate.storage.memory;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private static final AtomicInteger idCounter = new AtomicInteger(0);


    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted(Comparator.comparing(film -> film.getUsersLiked().size(),
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(count)
                .collect(Collectors.toSet());
    }

    @SneakyThrows
    @Override
    public Film create(Film film) {
        film.setId(idCounter.incrementAndGet());
        films.put(film.getId(), film);
        log.info("Создан фильм : {}", mapper.writeValueAsString(film));
        return film;
    }

    @SneakyThrows
    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Обновлен фильм : {}", mapper.writeValueAsString(film));
            return film;
        } else {
            throw new FilmNotFoundException(String.format("Фильм с id: %s не обнаружен", film.getId()));
        }
    }

    public Film getFilmById(Integer id) {
        if (!films.containsKey(id)) throw new FilmNotFoundException(String.format("Фильм с id: %s не обнаружен", id));
        return films.get(id);
    }
}
