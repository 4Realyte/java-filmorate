package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.Set;

public interface GenreDao {
    Set<FilmGenre> getAllGenres();
    FilmGenre create(FilmGenre genre);
    FilmGenre update(FilmGenre genre);
    FilmGenre getGenreById(int id);
    Set<FilmGenre> getAllGenresByFilmId(int id);
}
