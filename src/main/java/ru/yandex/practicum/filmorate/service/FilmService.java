package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmLikeDao;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.util.Collection;
import java.util.Set;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmLikeDao filmLikeDao;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       FilmLikeDao filmLikeDao, GenreDao genreDao, MpaDao mpaDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmLikeDao = filmLikeDao;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
    }

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
        filmLikeDao.addLike(film.getId(), user.getId());
        log.info("Пользователь {} поставил фильму {} лайк", user.getLogin(), film.getName());
    }

    public Set<FilmGenre> getGenres() {
        return genreDao.getAllGenres();
    }

    public FilmGenre getGenreById(int id) {
        return genreDao.getGenreById(id);
    }

    public Collection<Mpa> getAllMpa() {
        return mpaDao.getAllMpa();
    }

    public Mpa getMpaById(int id) {
        return mpaDao.getMpaById(id);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        filmLikeDao.deleteLike(film.getId(), user.getId());
        log.info("Пользователь {} убрал лайк с фильма {}", user.getLogin(), film.getName());
    }

    public Collection<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }
}
