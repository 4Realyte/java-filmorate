package ru.yandex.practicum.filmorate.dao;

import java.util.Set;

public interface UserFriendsDao {
   Set<Integer> getFriends(int userId, boolean mutual);
}
