package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Set<FilmGenre> getAllGenres() {
        String sql = "SELECT * FROM genre";
        List<FilmGenre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilmGenre(rs));
        return new LinkedHashSet<>(genres);
    }
    @Override
    public FilmGenre getGenreById(int id) {
        String sql = "SELECT * FROM genre WHERE genre_id=?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilmGenre(rs), id);
        } catch (EmptyResultDataAccessException ex) {
            throw new GenreNotFoundException(String.format("Жанр с id: %s не обнаружен", id));
        }
    }
    @Override
    public Set<FilmGenre> getAllGenresByFilmId(int id) {
        String sql = "SELECT * FROM genre WHERE genre_id IN (SELECT genre_id FROM film_genre WHERE film_id=?) " +
                "ORDER BY genre_id";
        List<FilmGenre> filmGenres = jdbcTemplate.query(sql, ((rs, rowNum) -> makeFilmGenre(rs)), id);
        return new LinkedHashSet<>(filmGenres);
    }
    private FilmGenre makeFilmGenre(ResultSet rs) throws SQLException {
        FilmGenre genre = new FilmGenre();
        genre.setName(rs.getString("name"));
        genre.setId(rs.getInt("genre_id"));
        return genre;
    }

    @Override
    public int create(FilmGenre genre) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("genre")
                .usingGeneratedKeyColumns("genre_id");
        return jdbcInsert.executeAndReturnKey(Map.of("name", genre.getName())).intValue();
    }
}
