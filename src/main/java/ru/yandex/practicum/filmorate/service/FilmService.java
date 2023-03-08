package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        film.getUsersLiked().add(user.getId());
        log.info("Пользователь {} поставил фильму {} лайк", user.getLogin(), film.getName());
    }

    public void deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        film.getUsersLiked().remove(user.getId());
        log.info("Пользователь {} убрал лайк с фильма {}", user.getLogin(), film.getName());
    }

    public Collection<Film> getPopularFilms(Integer count) {
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparing(film -> film.getUsersLiked().size(),
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(count)
                .collect(Collectors.toSet());
    }
}
