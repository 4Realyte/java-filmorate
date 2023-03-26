package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.Set;

@RequiredArgsConstructor
@Component
public class FilmGenreDaoImpl implements FilmGenreDao {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;

    public void create(Film film) {
        if (!film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genre(film_id,genre_id) VALUES(?,?) ON CONFLICT DO NOTHING";
            for (FilmGenre genre : film.getGenres()) {
                jdbcTemplate.update(sql, film.getId(), genre.getId());
            }
        }
    }

    public void update(Film film) {
        for (FilmGenre genre : film.getGenres()) {
            FilmGenre filmGenre = genreDao.getGenreById(genre.getId());
            if (filmGenre == null) {
                genreDao.create(genre);
            }
        }
    }

    @Override
    public Set<FilmGenre> getAllGenresByFilmId(int id) {
        return genreDao.getAllGenresByFilmId(id);
    }
}
