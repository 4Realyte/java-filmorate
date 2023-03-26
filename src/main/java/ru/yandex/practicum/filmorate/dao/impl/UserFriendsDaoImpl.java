package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserFriendsDao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Component
public class UserFriendsDaoImpl implements UserFriendsDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Set<Integer> getFriends(int userId, boolean mutual) {
        String sql = "SELECT friend_id FROM friends WHERE user_id=? AND status=?";
        List<Integer> friendsId = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("friend_id"),
                userId, true);
        return new HashSet<>(friendsId);
    }
}
