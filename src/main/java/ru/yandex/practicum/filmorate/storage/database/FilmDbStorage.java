package ru.yandex.practicum.filmorate.storage.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.FilmLikeDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;
    private final FilmGenreDao filmGenreDao;
    private final FilmLikeDao filmLikeDao;

    @Override
    public Collection<Film> getFilms() {
        String sql = "SELECT * FROM film";
        List<Film> films = jdbcTemplate.query(sql, ((rs, rowNum) -> makeFilm(rs)));
        return films;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpaRating(mpaDao.getMpaById(rs.getInt("rating_id")))
                .genres(filmGenreDao.getAllGenresByFilmId(rs.getInt("id")))
                .usersLiked(filmLikeDao.findUsersIdLiked(rs.getInt("id")))
                .build();
        return film;
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("film")
                .usingGeneratedKeyColumns("id");
        int id = jdbcInsert.executeAndReturnKey(Map.of("name", film.getName(),
                "description", film.getDescription(),
                "release_date", film.getReleaseDate(),
                "duration", film.getDuration(),
                "rating_id", mpaDao.getMpaId(film.getMpaRating()))).intValue();
        film.setId(id);
        filmGenreDao.create(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        getFilmById(film.getId());
        String sql = "UPDATE film SET name=?, description=?, release_date=?,duration=?, rating_id=? WHERE id=?";
        jdbcTemplate.update(sql, film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaRating(),
                mpaDao.getMpaId(film.getMpaRating()),
                film.getId());
        return film;
    }

    @Override
    public Film getFilmById(Integer id) {
        String sql = "SELECT * FROM film WHERE id=?";
        try {
            return jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> makeFilm(rs)), id);
        } catch (EmptyResultDataAccessException ex) {
            throw new FilmNotFoundException(String.format("Фильм с id: %s не обнаружен", id));
        }
    }
}
