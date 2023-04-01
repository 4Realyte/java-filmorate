package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Component
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa getMpaById(int id) {
        String sql = "SELECT * FROM mpa_rating WHERE rating_id=?";
        try {
            return jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> makeMpa(rs)), id);
        } catch (EmptyResultDataAccessException ex) {
            throw new MpaNotFoundException(String.format("MPA с id: %s не обнаружен", id));
        }
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa_rating";
        List<Mpa> mpas = jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
        return mpas;
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return new Mpa(rs.getString("mpa_name"), rs.getInt("rating_id"));
    }
}
