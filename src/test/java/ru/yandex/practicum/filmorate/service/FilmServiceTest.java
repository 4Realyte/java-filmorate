package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmServiceTest extends ServiceTest {

    @Test
    public void getFilms_shouldReturnCorrectSize() {
        assertEquals(0, filmService.getFilms().size());
        filmService.createFilm(Film.builder().id(1).name("titan").description("descript")
                .releaseDate(LocalDate.of(1987, 11, 12))
                .duration(20)
                .build());
        assertEquals(1, filmService.getFilms().size());
    }

    @Test
    public void createFilm_shouldCreateFilm() {
        Film film = filmService.createFilm(Film.builder().id(1).name("titan").description("descript")
                .releaseDate(LocalDate.of(1987, 11, 12))
                .duration(20)
                .build());
        assertNotNull(film);
    }

    @Test
    public void updateFilm_shouldThrowFilmNotException() {
        assertThrows(FilmNotFoundException.class, () ->
                filmService.updateFilm(Film.builder()
                        .id(1)
                        .name("titan")
                        .description("descript")
                        .releaseDate(LocalDate.of(1987, 11, 12))
                        .duration(20)
                        .build()));
    }

    @Test
    public void updateFilm_shouldUpdateTheFilm() {
        Film film = filmService.createFilm(Film.builder()
                .id(1)
                .name("titan")
                .description("descript")
                .releaseDate(LocalDate.of(1987, 11, 12))
                .duration(20)
                .build());
        film.setName("LalaLand");
        Film updated = filmService.updateFilm(film);
        assertEquals("LalaLand", updated.getName());
    }

    @Test
    public void getFilmById_shouldReturnPreviouslyAddedFilm() {
        Film film = filmService.createFilm(Film.builder().id(1).name("titan").description("descript")
                .releaseDate(LocalDate.of(1987, 11, 12))
                .duration(20)
                .build());
        //важно из-за того, что переменная counter static final, вне зависимости от создания нового объекта сервиса
        // id остаётся прежним при ранее запущенных тестах
        assertEquals(2, filmService.getFilmById(2).getId());
    }

    @Test
    public void getFilmById_shouldThrowFilmNotFoundException() {
        assertThrows(FilmNotFoundException.class, () -> filmService.getFilmById(1));
        filmService.createFilm(Film.builder().id(1).name("titan").description("descript")
                .releaseDate(LocalDate.of(1987, 11, 12))
                .duration(30)
                .build());
        assertThrows(FilmNotFoundException.class, () -> filmService.getFilmById(2));
    }
}
