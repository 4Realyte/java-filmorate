package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.Set;

@RequiredArgsConstructor
@Component
public class FilmGenreDaoImpl implements FilmGenreDao {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;

    @Override
    public void create(Film film) {
        if (!film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genre(film_id,genre_id) VALUES(?,?) ON CONFLICT DO NOTHING";
            for (FilmGenre genre : film.getGenres()) {
                jdbcTemplate.update(sql, film.getId(), genreDao.getGenreId(genre.getName()));
            }
        }
    }

    @Override
    public void update(Film film) {
        if (checkIfGenreExists(film)) {
            delete(film);
            create(film);
        } else {
            throw new GenreNotFoundException("При обновлении жанра фильма произошла ошибка");
        }
    }

    private boolean checkIfGenreExists(Film film) {
        boolean isExist = true;
        for (FilmGenre genre : film.getGenres()) {
            int id = genreDao.getGenreId(genre.getName());
            if (id == -1) {
                isExist = false;
            }
        }
        return isExist;
    }

    public void delete(Film film) {
        String sql = "DELETE FROM film_genre WHERE film_id=?";
        jdbcTemplate.update(sql, film.getId());
    }

    @Override
    public Set<FilmGenre> getAllGenresByFilmId(int id) {
        return genreDao.getAllGenresByFilmId(id);
    }
}
