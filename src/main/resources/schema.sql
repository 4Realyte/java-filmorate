CREATE TABLE IF NOT EXISTS user
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    varchar,
    login    varchar NOT NULL,
    name     varchar,
    birthday date,
    UNIQUE (login),
    CONSTRAINT not_empty_email CHECK (email <> '' AND login <> ''),
    CONSTRAINT date_constr CHECK (birthday < CURRENT_DATE)
);
CREATE TABLE IF NOT EXISTS mpa_rating
(
    rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name      varchar(30) NOT NULL,
    CONSTRAINT not_empty_mpa CHECK (name <> ''),
    UNIQUE (name)
);
CREATE TABLE IF NOT EXISTS film
(
    id           INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         varchar,
    description  varchar(200),
    release_date date CHECK (release_date > date '1895-12-28'),
    duration     INTEGER,
    rating_id    INTEGER REFERENCES mpa_rating (rating_id) ON DELETE SET NULL,
    CONSTRAINT not_empty_descr CHECK (duration > 0 AND description <> '' AND name <> '')
);
CREATE TABLE IF NOT EXISTS film_like
(
    film_id INTEGER REFERENCES film (id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES user (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
);
CREATE TABLE IF NOT EXISTS friends
(
    user_id   INTEGER REFERENCES user (id) ON DELETE CASCADE,
    friend_id INTEGER REFERENCES user (id) ON DELETE CASCADE,
    status    bool,
    PRIMARY KEY (user_id, friend_id)
);
CREATE TABLE IF NOT EXISTS genre
(
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name     varchar(50) NOT NULL,
    CONSTRAINT not_empty_gen CHECK (name <> ''),
    UNIQUE (name)
);
CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  INTEGER REFERENCES film (id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genre (genre_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);
