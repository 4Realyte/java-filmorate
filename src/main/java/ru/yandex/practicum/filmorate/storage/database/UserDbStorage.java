package ru.yandex.practicum.filmorate.storage.database;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserFriendsDao;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Slf4j
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserFriendsDao userFriendsDao;

    @Override
    public Collection<User> getUsers() {
        String sql = "SELECT * FROM user";
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
        return users;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        User user = User.builder()
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .friends(userFriendsDao.getFriends(rs.getInt("id")))
                .id(rs.getInt("id"))
                .build();
        return user;
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("user")
                .usingGeneratedKeyColumns("id");
        int id = jdbcInsert.executeAndReturnKey(Map.of("email", user.getEmail(),
                "login", user.getLogin(),
                "name", user.getName(),
                "birthday", user.getBirthday())).intValue();
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        getUserById(user.getId());
        String sql = "UPDATE user SET email=?,login=?,name=?,birthday=? WHERE id=?";
        jdbcTemplate.update(sql,user.getEmail(),user.getLogin(),user.getName(),user.getBirthday(),user.getId());
        return user;
    }

    @Override
    public User getUserById(Integer id) {
        String sql = "SELECT * FROM user WHERE id=?";
        try {
            return jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> makeUser(rs)), id);
        } catch (EmptyResultDataAccessException ex) {
            throw new UserNotFoundException(String.format("Пользователь с id: %s не обнаружен", id));
        }
    }
}
