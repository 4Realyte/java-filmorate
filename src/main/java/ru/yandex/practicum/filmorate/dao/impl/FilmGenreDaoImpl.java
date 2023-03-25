package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.List;

@RequiredArgsConstructor
@Component
public class FilmGenreDaoImpl implements FilmGenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public EnumSet<FilmGenre> getAllGenresById(int id) {
        String sql = "SELECT name FROM genre WHERE genre_id IN (SELECT genre_id FROM film_genre WHERE film_id=?)";
        List<FilmGenre> filmGenres = jdbcTemplate.query(sql, ((rs, rowNum) -> makeFilmGenre(rs)), id);
        return EnumSet.copyOf(filmGenres);
    }

    private FilmGenre makeFilmGenre(ResultSet rs) throws SQLException {
        return FilmGenre.valueOf(rs.getString("name"));
    }
}
