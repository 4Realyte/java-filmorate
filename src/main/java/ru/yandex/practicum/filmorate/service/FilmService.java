package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        film.getUsersLiked().add(userId);
        log.info("Пользователь {} поставил фильму {} лайк", userStorage.getUserById(userId).getLogin(), film.getName());
    }

    public void deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        film.getUsersLiked().remove(userId);
        log.info("Пользователь {} убрал лайк с фильма {}", userStorage.getUserById(userId).getLogin(), film.getName());
    }

    public Collection<Film> getPopularFilms(Integer count) {
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparing(Film::getId, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(count)
                .collect(Collectors.toSet());
    }
}
