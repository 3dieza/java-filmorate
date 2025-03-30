//package ru.yandex.practicum.filmorate;
//
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.model.User;
//
//import java.time.Duration;
//import java.time.LocalDate;
//import java.time.Month;
//import java.util.Random;
//
//public class Mocks {
//
//    private static final Random random = new Random();
//
//    public static Film getRandomFilm() {
//        Film film = new Film();
//        film.setName("Film " + getRandomString(5));
//        film.setDescription("Description " + getRandomString(10));
//        film.setReleaseDate(LocalDate.of(2000, Month.JANUARY, random.nextInt(12) + 1));
//        film.setDuration(Duration.ofMinutes(random.nextInt(180) + 30));
//        return film;
//    }
//
//    private static String getRandomString(int length) {
//        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
//        StringBuilder stringBuilder = new StringBuilder();
//        for (int i = 0; i < length; i++) {
//            int index = random.nextInt(characters.length());
//            stringBuilder.append(characters.charAt(index));
//        }
//        return stringBuilder.toString();
//    }
//
//    public static User getRandomUser() {
//        User user = new User();
//        user.setEmail("user" + getRandomString(5) + "@example.com");
//        user.setLogin("login" + getRandomString(5));
//        user.setName(user.getLogin());
//        user.setBirthday(LocalDate.of(1990, Month.JANUARY, random.nextInt(28) + 1));
//        return user;
//    }
//}