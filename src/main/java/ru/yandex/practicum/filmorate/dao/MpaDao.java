package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;

public interface MpaDao {
    MPA getMpaById(int id);

    int getMpaId(MPA mpa);

    Collection<MPA> getAllMpa();
}
