package ru.yandex.practicum.filmorate.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping
@Validated
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping(value = "/films/{id}")
    public Film getFilmById(@PathVariable
                            @PositiveOrZero(message = "Параметр id не может быть отрицательным") Integer id) {
        return filmService.getFilmById(id);

    }

    @GetMapping("/genres")
    public Collection<FilmGenre> getFilmGenres() {
        return filmService.getGenres();
    }

    @GetMapping("/genres/{id}")
    public FilmGenre getFilmGenreById(@PathVariable
                                      @PositiveOrZero int id) {
        return filmService.getGenreById(id);
    }

    @GetMapping("/mpa")
    public Collection<MPA> getMpas() {
        return filmService.getAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public MPA getMpaById(@PathVariable int id) {
        return filmService.getMpaById(id);
    }

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable("id") @PositiveOrZero(message = "Параметр id не может быть отрицательным")
                        Integer filmId,
                        @PathVariable
                        Integer userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") @PositiveOrZero(message = "Параметр id не может быть отрицательным")
                           Integer filmId,
                           @PathVariable
                           Integer userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/films/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10")
                                            @PositiveOrZero(message = "Параметр count не может быть отрицательным")
                                            Integer count) {
        return filmService.getPopularFilms(count);
    }
}
