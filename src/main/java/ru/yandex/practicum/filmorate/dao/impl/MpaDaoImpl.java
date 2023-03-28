package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Component
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public MPA getMpaById(int id) {
        String sql = "SELECT name FROM mpa_rating WHERE rating_id=?";
        try{
            return jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> makeMpa(rs)), id);
        } catch (EmptyResultDataAccessException ex){
            throw new MpaNotFoundException(String.format("MPA с id: %s не обнаружен", id));
        }
    }

    @Override
    public int getMpaId(MPA mpa) {
        String sql = "SELECT rating_id FROM mpa_rating WHERE name=?";
        int id = jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> rs.getInt("rating_id")), mpa.getName());
        return id;
    }

    @Override
    public Collection<MPA> getAllMpa() {
        String sql = "SELECT * FROM mpa_rating";
        List<MPA> mpas = jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
        return mpas;
    }

    private MPA makeMpa(ResultSet rs) throws SQLException {
        return MPA.valueOf(rs.getString("name").replaceFirst("-",""));
    }
}
