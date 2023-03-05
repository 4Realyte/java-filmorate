package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь {} успешно добавил в друзья {}", user.getLogin(), friend.getLogin());
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь {} успешно удалил из друзей {}", user.getLogin(), friend.getLogin());
    }

    public Collection<User> getFriends(Integer userId) {
        return userStorage.getUserById(userId)
                .getFriends()
                .stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toSet());
    }

    public Collection<User> findCommonFriends(Integer userId, Integer friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        return user.getFriends().stream()
                .filter(us -> friend.getFriends().contains(us))
                .map(userStorage::getUserById)
                .collect(Collectors.toSet());
    }
}
