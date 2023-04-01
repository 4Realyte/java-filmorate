package ru.yandex.practicum.filmorate.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@Validated
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping(value = "/{id}")
    public Film getFilmById(@PathVariable
                            @PositiveOrZero(message = "Параметр id не может быть отрицательным") Integer id) {
        return filmService.getFilmById(id);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") @PositiveOrZero(message = "Параметр id не может быть отрицательным")
                        Integer filmId,
                        @PathVariable
                        Integer userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") @PositiveOrZero(message = "Параметр id не может быть отрицательным")
                           Integer filmId,
                           @PathVariable
                           Integer userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10")
                                            @PositiveOrZero(message = "Параметр count не может быть отрицательным")
                                            Integer count) {
        return filmService.getPopularFilms(count);
    }
}
