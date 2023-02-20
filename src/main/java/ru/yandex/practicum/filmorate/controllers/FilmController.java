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

    private Film validate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("максимальная длина описания — 200 символов");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("дата релиза — не раньше 28.12.1895");
        } else if (film.getDuration() < 0) {
            throw new ValidationException("продолжительность фильма должна быть положительной!");
        }
        return film;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) throws JsonProcessingException {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("дата релиза — не раньше 28.12.1895");
        }
        film.setId(idCounter.incrementAndGet());
        films.put(film.getId(), film);
        log.info("Создан фильм : {}", mapper.writeValueAsString(film));
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) throws JsonProcessingException {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Обновлен фильм : {}", mapper.writeValueAsString(film));
            return film;
        } else {
            throw new FilmNotFoundException("фильма не существует");
        }
    }
}
