INSERT INTO mpa_rating
SELECT *
FROM (SELECT 1, 'G'
      UNION
      SELECT 2, 'PG'
      UNION
      SELECT 3, 'PG-13'
      UNION
      SELECT 4, 'R'
      UNION
      SELECT 5, 'NC-17') x
WHERE NOT EXISTS(SELECT * FROM mpa_rating);
INSERT INTO genre
SELECT *
FROM (SELECT 1, 'Комедия'
      UNION
      SELECT 2, 'Драма'
      UNION
      SELECT 3, 'Мультфильм'
      UNION
      SELECT 4, 'Триллер'
      UNION
      SELECT 5, 'Документальный'
      UNION
      SELECT 6, 'Боевик') x
WHERE NOT EXISTS(SELECT * FROM genre);

-- MERGE INTO mpa_rating (name)  VALUES ('G'),('PG'),('PG-13'),('R'),('NC-17');
-- MERGE INTO genre (name) VALUES ('Комедия'),('Драма'),('Мультфильм'),('Триллер'),('Документальный'),('Боевик');