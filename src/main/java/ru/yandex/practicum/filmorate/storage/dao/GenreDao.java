package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.Set;

public interface GenreDao {
    Set<FilmGenre> getAllGenres();

    int create(FilmGenre genre);

    FilmGenre getGenreById(int id);

    Set<FilmGenre> getAllGenresByFilmId(int id);
}
