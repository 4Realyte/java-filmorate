package ru.yandex.practicum.filmorate.dao;

import java.util.Set;

public interface FilmLikeDao {
    Set<Integer> findUsersIdLiked(int filmId);
}
