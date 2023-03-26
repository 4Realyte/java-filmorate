package ru.yandex.practicum.filmorate.dao;

import java.util.Set;

public interface FilmLikeDao {
    Set<Integer> findUsersIdLiked(int filmId);

    void addLike(Integer filmId, Integer userId);
    void deleteLike(Integer filmId, Integer userId);
}
