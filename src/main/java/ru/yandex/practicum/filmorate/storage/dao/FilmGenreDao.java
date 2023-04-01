package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.Set;

public interface FilmGenreDao {
    void create(Film film);

    Set<FilmGenre> getAllGenresByFilmId(int id);

    void update(Film film);
}
