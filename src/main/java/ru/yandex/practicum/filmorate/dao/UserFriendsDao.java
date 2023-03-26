package ru.yandex.practicum.filmorate.dao;

import java.util.Set;

public interface UserFriendsDao {
    Set<Integer> getFriends(int userId);

    void addFriend(int userId, int friendId);
    void updateFriendStatus(int userId, int friendId, boolean mutual);
    void deleteFriend(int userId, int friendId);
}
