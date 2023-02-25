package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private static final AtomicInteger idCounter = new AtomicInteger(0);
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }
    @PostMapping
    public Film create(@Valid @RequestBody Film film) throws JsonProcessingException, ValidationException {
        film.setId(idCounter.incrementAndGet());
        films.put(film.getId(), film);
        log.info("Создан фильм : {}", mapper.writeValueAsString(film));
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) throws JsonProcessingException, ValidationException {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Обновлен фильм : {}", mapper.writeValueAsString(film));
            return film;
        } else {
            throw new FilmNotFoundException("фильма не существует");
        }
    }
}
