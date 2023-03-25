package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.MPA;

public interface MpaDao {
    MPA getMpaById(int id);
    int getMpaId(MPA mpa);
}
