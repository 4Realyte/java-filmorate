package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserFriendsDao;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

   private final UserStorage userStorage;
    private UserFriendsDao userFriendsDao;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    public User createUser(User user) {
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    @Autowired
    public void setUserFriendsDao(UserFriendsDao userFriendsDao) {
        this.userFriendsDao = userFriendsDao;
    }

    public void addFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        friend.getFriends().add(user.getId());
        userFriendsDao.addFriend(userId, friendId);
        log.info("Пользователь - {} успешно добавил в друзья пользователя - {}", user.getLogin(), friend.getLogin());
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().remove(friendId);
        userFriendsDao.deleteFriend(userId,friendId);
        log.info("Пользователь - {} успешно удалил из друзей пользователя - {}", user.getLogin(), friend.getLogin());
    }

    public Collection<User> getFriends(Integer userId) {
        return userStorage.getUserById(userId)
                .getFriends()
                .stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toCollection(LinkedList::new));
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
