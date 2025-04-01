-- Рейтинги (MPA)
MERGE INTO rating (id, name) KEY(id)
VALUES
    (1, 'G'),
    (2, 'PG'),
    (3, 'PG-13'),
    (4, 'R'),
    (5, 'NC-17');

-- Жанры
MERGE INTO genre (id, name) KEY(id)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');

-- Пользователи
MERGE INTO users (id, email, login, name, birthday, status) KEY(id)
VALUES (1, 'user1@example.com', 'user1', 'Alice', '1990-01-01', 'active'),
       (2, 'user2@example.com', 'user2', 'Bob', '1988-05-15', 'active'),
       (3, 'user3@example.com', 'user3', 'Charlie', '1992-08-20', 'inactive');

-- Автоинкремент для новых пользователей
ALTER TABLE users ALTER COLUMN id RESTART WITH 4;

-- Фильмы
MERGE INTO film (id, name, description, release_date, duration, rating_id) KEY(id)
VALUES (1, 'Inception', 'A mind-bending thriller', '2010-07-16', 120, 3),
       (2, 'The Social Network', 'The story of Facebook', '2010-10-01', 120, 2),
       (3, 'Toy Story', 'Animated fun with toys', '1995-11-22', 81, 1);

-- Автоинкремент для новых фильмов (если нужно)
ALTER TABLE film ALTER COLUMN id RESTART WITH 4;

-- Жанры фильмов
MERGE INTO film_genre (film_id, genre_id) KEY(film_id, genre_id)
VALUES (1, 4),
       (1, 5),
       (2, 2),
       (3, 1);

-- Лайки фильмов
MERGE INTO film_likes (film_id, user_id) KEY(film_id, user_id)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (3, 3);

-- Друзья
MERGE INTO user_friends (user_id, friend_id, status) KEY(user_id, friend_id)
VALUES (1, 2, 'added'),
       (1, 3, 'added');