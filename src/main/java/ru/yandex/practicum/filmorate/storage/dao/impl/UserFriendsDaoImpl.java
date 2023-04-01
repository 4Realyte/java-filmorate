package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.dao.UserFriendsDao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Component
public class UserFriendsDaoImpl implements UserFriendsDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Set<Integer> getFriends(int userId) {
        String sql = "SELECT friend_id FROM friends WHERE user_id=?";
        List<Integer> friendsId = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("friend_id"),
                userId);
        return new HashSet<>(friendsId);
    }

    private boolean checkFriendStatus(int userId, int friendId) {
        return getFriends(userId).contains(friendId) && getFriends(friendId).contains(userId);
    }

    private void updateFriendStatus(int userId, int friendId, boolean mutual) {
        String sql = "UPDATE friends SET status=? WHERE user_id=? AND friend_id=?";
        jdbcTemplate.update(sql, mutual, userId, friendId);
        jdbcTemplate.update(sql, mutual, friendId, userId);
    }

    public void addFriend(int userId, int friendId) {
        String sql = "INSERT INTO friends(user_id,friend_id,status) VALUES(?,?,?)";
        jdbcTemplate.update(sql, userId, friendId, false);
        if (checkFriendStatus(userId, friendId)) {
            updateFriendStatus(userId, friendId, true);
        }
    }

    public void deleteFriend(int userId, int friendId) {
        String sql = "DELETE FROM friends WHERE user_id=? AND friend_id=?";
        jdbcTemplate.update(sql, userId, friendId);
        if (getFriends(friendId).contains(userId)) {
            updateFriendStatus(friendId, userId, false);
        }
    }
}
