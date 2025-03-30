package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
@Primary
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film createFilm(Film film) {
        String sql = "INSERT INTO film (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, Math.toIntExact(film.getDuration().toMinutes()));
            ps.setObject(5, film.getMpa() != null ? film.getMpa().getId() : null);
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            film.setId(keyHolder.getKey().longValue());
        }

        // Добавление жанров
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String genreSql = "MERGE INTO film_genre (film_id, genre_id) KEY(film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(genreSql, film.getId(), genre.getId());
            }
        }

        // Подгружаем жанры
        String genreSelectSql = "SELECT g.id, g.name FROM genre g JOIN film_genre fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(genreSelectSql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, film.getId());
        film.setGenres(genres);

        // Подгружаем рейтинг
        if (film.getMpa() != null && film.getMpa().getId() != 0) {
            String ratingSql = "SELECT name FROM rating WHERE id = ?";
            List<String> ratingNames = jdbcTemplate.query(ratingSql, (rs, rowNum) -> rs.getString("name"), film.getMpa().getId());
            if (!ratingNames.isEmpty()) {
                film.getMpa().setName(ratingNames.get(0));
            }
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String checkSql = "SELECT COUNT(*) FROM film WHERE id = ?";
        Integer exists = jdbcTemplate.queryForObject(checkSql, Integer.class, film.getId());
        if (exists == null || exists == 0) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден");
        }

        String sql = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                (int) film.getDuration().toMinutes(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );
        return film;
    }

    @Override
    public Collection<Film> getFilms() {
        String sql = "SELECT * FROM film";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(Duration.ofMinutes(rs.getInt("duration")));
            Integer ratingId = rs.getObject("rating_id", Integer.class);
            if (ratingId != null) {
                film.setMpa(new Rating(ratingId, null));
            }
            return film;
        });
    }

    @Override
    public boolean isReleaseDateValid(LocalDate releaseDate) {
        return releaseDate != null && !releaseDate.isBefore(LocalDate.of(1895, 12, 28));
    }

    @Override
    public Film getFilmById(Long id) {
        String sql = "SELECT * FROM film WHERE id = ?";
        Film film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Film f = new Film();
            f.setId(rs.getLong("id"));
            f.setName(rs.getString("name"));
            f.setDescription(rs.getString("description"));
            f.setReleaseDate(rs.getDate("release_date").toLocalDate());
            f.setDuration(Duration.ofMinutes(rs.getInt("duration")));
            Integer ratingId = rs.getObject("rating_id", Integer.class);
            if (ratingId != null) {
                String ratingName = jdbcTemplate.queryForObject(
                        "SELECT name FROM rating WHERE id = ?", String.class, ratingId);
                f.setMpa(new Rating(ratingId, ratingName));
            }
            return f;
        }, id);

        // Жанры
        String genreSql = """
                    SELECT g.id, g.name
                    FROM genre g
                    JOIN film_genre fg ON g.id = fg.genre_id
                    WHERE fg.film_id = ?
                """;
        List<Genre> genres = jdbcTemplate.query(genreSql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, id);
        film.setGenres(genres);

        return film;
    }

    public Film mapRowToFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(Duration.ofMinutes(rs.getInt("duration")));

        Integer ratingId = rs.getObject("rating_id", Integer.class);
        if (ratingId != null) {
            String ratingName = jdbcTemplate.queryForObject(
                    "SELECT name FROM rating WHERE id = ?", String.class, ratingId);
            film.setMpa(new Rating(ratingId, ratingName));
        }

        return film;
    }

    public List<Genre> getGenres(Long filmId) {
        String sql = """
                    SELECT g.id, g.name
                    FROM genre g
                    JOIN film_genre fg ON g.id = fg.genre_id
                    WHERE fg.film_id = ?
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId);
    }
}