package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmLikeDao;
import ru.yandex.practicum.filmorate.storage.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;
    private FilmLikeDao filmLikeDao;
    private GenreDao genreDao;
    private MpaDao mpaDao;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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

    @Autowired
    public void setFilmLikeDao(FilmLikeDao filmLikeDao) {
        this.filmLikeDao = filmLikeDao;
    }

    @Autowired
    public void setMpaDao(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    @Autowired
    public void setGenreDao(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Set<FilmGenre> getGenres() {
        return genreDao.getAllGenres();
    }

    public FilmGenre getGenreById(int id) {
        return genreDao.getGenreById(id);
    }

    public Collection<MPA> getAllMpa() {
        return mpaDao.getAllMpa();
    }

    public MPA getMpaById(int id) {
        return mpaDao.getMpaById(id);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        filmLikeDao.deleteLike(film.getId(), user.getId());
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
