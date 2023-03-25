package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.EnumSet;

public interface FilmGenreDao {
    EnumSet<FilmGenre> getAllGenresById(int id);
}
