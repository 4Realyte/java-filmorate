package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
@Component
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public MPA getMpaById(int id) {
        String sql = "SELECT name FROM mpa_rating WHERE rating_id=?";
        MPA mpa = jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> makeMpa(rs)), id);
        return mpa;
    }

    @Override
    public int getMpaId(MPA mpa) {
        String sql = "SELECT rating_id FROM mpa_rating WHERE name=?";
        int id = jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> rs.getInt("rating_id")), mpa.name());
        return id;
    }

    private MPA makeMpa(ResultSet rs) throws SQLException {
        return MPA.valueOf(rs.getString("name"));
    }
}
