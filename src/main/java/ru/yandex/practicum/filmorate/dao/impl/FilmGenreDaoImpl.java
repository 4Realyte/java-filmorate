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

    @Override
    public void create(Film film) {
        if (!film.getGenres().isEmpty()) {
            film.getGenres().stream().map(FilmGenre::getId).forEach(genreDao::getGenreById);
            String sql = "INSERT INTO film_genre(film_id,genre_id) VALUES(?,?)";
            for (FilmGenre genre : film.getGenres()) {
                jdbcTemplate.update(sql, film.getId(), genre.getId());
            }
            film.setGenres(genreDao.getAllGenresByFilmId(film.getId()));
        }
    }

    @Override
    public void update(Film film) {
        delete(film);
        create(film);
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
