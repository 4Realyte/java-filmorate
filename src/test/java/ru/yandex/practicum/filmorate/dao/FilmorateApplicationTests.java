package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FilmLikeDao;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;
import ru.yandex.practicum.filmorate.storage.dao.UserFriendsDao;
import ru.yandex.practicum.filmorate.storage.database.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.database.UserDbStorage;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final FilmLikeDao filmLikeDao;
    private final UserFriendsDao userFriendsDao;
    private final MpaDao mpaDao;
    private final JdbcTemplate jdbcTemplate;
    private Film film;
    private User user;
    private User friend;


    @BeforeEach
    public void init() {
        film = Film.builder().name("titan").description("descript")
                .releaseDate(LocalDate.of(1987, 11, 12))
                .duration(200)
                .usersLiked(Collections.emptySet())
                .mpa(new Mpa("PG", 2))
                .genres(Set.of(new FilmGenre("Комедия", 1)))
                .build();
        user = User.builder()
                .friends(Collections.emptySet())
                .email("alex@mail.com")
                .name("Alex")
                .login("Ken")
                .birthday(LocalDate.of(1994, 10, 23))
                .build();
        friend = User.builder()
                .friends(Collections.emptySet())
                .email("mushi@mail.com")
                .name("Michael")
                .login("MIHA")
                .birthday(LocalDate.of(1991, 12, 11))
                .build();

    }

    @Test
    public void getUsersTest() {
        String sql = "DELETE FROM user";
        jdbcTemplate.execute(sql);
        assertEquals(0, userStorage.getUsers().size());
        userStorage.create(user);
        assertEquals(1, userStorage.getUsers().size());
    }

    @Test
    public void addFriendTest() {
        User user1 = userStorage.create(user);
        User friend1 = userStorage.create(friend);
        userFriendsDao.addFriend(user1.getId(), friend1.getId());
        assertThat(userFriendsDao.getFriends(user1.getId()).size()).isEqualTo(1);
        assertThat(userFriendsDao.getFriends(friend1.getId()).size()).isEqualTo(0);
        userFriendsDao.addFriend(friend1.getId(), user1.getId());
        assertThat(userFriendsDao.getFriends(friend1.getId()).size()).isEqualTo(1);
        String sql = "SELECT status FROM friends WHERE user_id=?";
        Boolean status = jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> rs.getBoolean("status"), user1.getId());
        assertEquals(true, status);
    }

    @Test
    public void deleteFriendTest() {
        jdbcTemplate.execute("DELETE FROM friends");
        User user1 = userStorage.create(user);
        User friend1 = userStorage.create(friend);
        userFriendsDao.addFriend(user1.getId(), friend1.getId());
        userFriendsDao.addFriend(friend1.getId(), user1.getId());
        String sql = "SELECT status FROM friends WHERE user_id=?";
        Boolean status = jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> rs.getBoolean("status"), user1.getId());
        assertEquals(true, status);
        userFriendsDao.deleteFriend(user1.getId(), friend1.getId());
        assertThat(userFriendsDao.getFriends(user1.getId()).size()).isEqualTo(0);
        assertThat(userFriendsDao.getFriends(friend1.getId()).size()).isEqualTo(1);
        Boolean afterDelStatus = jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> rs.getBoolean("status"), friend1.getId());
        assertEquals(false, afterDelStatus);
    }

    @Test
    public void getFriendsTest() {
        User user1 = userStorage.create(user);
        User friend1 = userStorage.create(friend);
        userFriendsDao.addFriend(user1.getId(), friend1.getId());
        userFriendsDao.addFriend(friend1.getId(), user1.getId());
        assertThat(userFriendsDao.getFriends(user1.getId()).size()).isEqualTo(1);
        assertThat(userFriendsDao.getFriends(friend1.getId()).size()).isEqualTo(1);
    }

    @Test
    public void getMpaByIdTest() {
        assertThrows(MpaNotFoundException.class, () -> mpaDao.getMpaById(9999));
        assertThat(mpaDao.getMpaById(1).getName()).isEqualTo("G");
    }

    @Test
    public void getAllMpaTest() {
        assertThat(mpaDao.getAllMpa().size()).isEqualTo(5);
    }

    @Test
    public void addLikeTest() {
        Film crFilm = filmStorage.create(film);
        User crUser = userStorage.create(user);
        filmLikeDao.addLike(crFilm.getId(), crUser.getId());
        int id = filmLikeDao.findUsersIdLiked(crFilm.getId()).iterator().next();
        assertThat(id).isEqualTo(crUser.getId());
    }

    @Test
    public void deleteLike() {
        Film crFilm = filmStorage.create(film);
        User crUser = userStorage.create(user);
        filmLikeDao.addLike(crFilm.getId(), crUser.getId());
        filmLikeDao.deleteLike(crFilm.getId(), crUser.getId());
        assertThat(filmLikeDao.findUsersIdLiked(crFilm.getId())).isEqualTo(Collections.emptySet());
    }

    @Test
    public void createUserTest() {
        User created = userStorage.create(user);
        User dbUser = userStorage.getUserById(created.getId());
        assertEquals(created, dbUser);
        assertThat(created.getName()).isEqualTo("Alex");
    }

    @Test
    public void updateUserTest() {
        User created = userStorage.create(user);
        created.setName("Bob");
        created.setLogin("John Doe");
        created.setEmail("bob@gmail.com");
        userStorage.update(created);
        User dbUser = userStorage.getUserById(created.getId());
        assertThat(created.getLogin()).isEqualTo("John Doe");

        dbUser.setId(9999);
        assertThrows(UserNotFoundException.class, () -> userStorage.update(dbUser));
    }

    @Test
    public void getUserByIdTest() {
        assertThrows(UserNotFoundException.class, () -> userStorage.getUserById(9999));
        User created = userStorage.create(user);
        User dbUser = userStorage.getUserById(created.getId());
        assertThat(dbUser.getLogin()).isEqualTo("Ken");
    }

    @Test
    public void getFilmsTest() {
        assertEquals(0, filmStorage.getFilms().size());
        filmStorage.create(film);
        assertEquals(1, filmStorage.getFilms().size());
    }

    @Test
    public void createFilmTest() {
        Film created = filmStorage.create(film);
        Film dbFilm = filmStorage.getFilmById(created.getId());
        assertEquals(created, dbFilm);
    }

    @Test
    public void updateFilmTest() {
        Film created = filmStorage.create(film);
        created.setMpa(new Mpa("NC-17", 5));
        created.setName("Great");
        created.setDuration(100);
        created.setGenres(Set.of(new FilmGenre("Драма", 2)));
        Film updated = filmStorage.update(created);
        Film dbFilm = filmStorage.getFilmById(updated.getId());

        assertEquals(updated, dbFilm);
        assertEquals(dbFilm.getGenres().iterator().next().getName(), "Драма");
        dbFilm.setId(9878);
        assertThrows(FilmNotFoundException.class, () -> filmStorage.update(dbFilm));
    }

    @Test
    public void getFilmByIdTest() {
        assertThrows(FilmNotFoundException.class, () -> filmStorage.getFilmById(9999));
        Film created = filmStorage.create(film);
        Film dbFilm = filmStorage.getFilmById(created.getId());
        assertThat(dbFilm.getName()).isEqualTo("titan");
        assertEquals(dbFilm, created);
    }
}
