package ru.yandex.practicum.filmorate.storage.dao;

import java.util.Set;

public interface UserFriendsDao {
    Set<Integer> getFriends(int userId);

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);
}
