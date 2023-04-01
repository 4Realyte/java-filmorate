package ru.yandex.practicum.filmorate.storage.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.storage.dao.FilmLikeDao;

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
    private final FilmGenreDao filmGenreDao;
    private final FilmLikeDao filmLikeDao;
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public Collection<Film> getFilms() {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id, m.mpa_name " +
                "FROM film AS f INNER JOIN mpa_rating as m ON f.rating_id=m.rating_id";
        List<Film> films = jdbcTemplate.query(sql, ((rs, rowNum) -> makeFilm(rs)));
        return films;
    }

    public Collection<Film> getPopularFilms(int count) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id,m.mpa_name,COUNT(fl.USER_ID) as likes " +
                "FROM film as f " +
                "LEFT JOIN FILM_LIKE FL on f.ID = FL.FILM_ID " +
                "INNER JOIN MPA_RATING as m ON f.RATING_ID=m.RATING_ID " +
                "GROUP BY f.ID " +
                "ORDER BY COUNT(USER_ID) DESC " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, ((rs, rowNum) -> makePopularFilm(rs)), count);
        return films;
    }

    private Film makePopularFilm(ResultSet rs) throws SQLException {
        Film film = makeFilm(rs);
        film.setUsersLiked(filmLikeDao.findUsersIdLiked(film.getId()));
        return film;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        int filmId = rs.getInt("id");
        return Film.builder()
                .id(filmId)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getString("mpa_name"), rs.getInt("rating_id")))
                .genres(filmGenreDao.getAllGenresByFilmId(filmId))
                .build();
    }

    private Film checkIfExists(Film film) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id, m.mpa_name " +
                "FROM film AS f " +
                "INNER JOIN mpa_rating AS m ON f.rating_id=m.rating_id " +
                "WHERE name=? AND description=? AND release_date=? AND duration=? AND f.rating_id=?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs),
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId());
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }

    }

    @Override
    @SneakyThrows
    public Film create(Film film) {
        Film dbFilm;
        if ((dbFilm = checkIfExists(film)) != null) {
            return dbFilm;
        } else {
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("film")
                    .usingGeneratedKeyColumns("id");
            int id = jdbcInsert.executeAndReturnKey(Map.of("name", film.getName(),
                    "description", film.getDescription(),
                    "release_date", film.getReleaseDate(),
                    "duration", film.getDuration(),
                    "rating_id", film.getMpa().getId())).intValue();
            film.setId(id);
            filmGenreDao.create(film);
            log.info("Создан фильм : {}", mapper.writeValueAsString(film));
            return film;
        }
    }

    @Override
    @SneakyThrows
    public Film update(Film film) {
        getFilmById(film.getId());
        String sql = "UPDATE film SET name=?, description=?, release_date=?,duration=?, rating_id=? WHERE id=?";
        jdbcTemplate.update(sql, film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        filmGenreDao.update(film);
        log.info("Обновлен фильм : {}", mapper.writeValueAsString(film));
        return film;
    }

    @Override
    public Film getFilmById(Integer id) {
        String sql = "SELECT * FROM film AS f INNER JOIN mpa_rating as m ON f.rating_id=m.rating_id WHERE id=?";
        try {
            return jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> makeFilm(rs)), id);
        } catch (EmptyResultDataAccessException ex) {
            throw new FilmNotFoundException(String.format("Фильм с id: %s не обнаружен", id));
        }
    }
}
