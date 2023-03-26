package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmLikeDao;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Component
public class FilmLikeDaoImpl implements FilmLikeDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Set<Integer> findUsersIdLiked(int filmId) {
        String sql = "SELECT user_id FROM film_like WHERE film_id=?";
        List<Integer> usersId = jdbcTemplate.query(sql, ((rs, rowNum) -> rs.getInt("user_id")), filmId);
        if (usersId.isEmpty()) return Collections.emptySet();

        return new HashSet<>(usersId);
    }

    public void addLike(Integer filmId, Integer userId) {
        String sql = "INSERT INTO film_like(film_id,user_id) VALUES(?,?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        String sql = "DELETE FROM film_like WHERE film_id=? AND user_id=?";
        jdbcTemplate.update(sql, filmId, userId);
    }

}
