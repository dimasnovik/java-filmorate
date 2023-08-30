package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.LocalDateAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmControllerTest {
    private static HttpClient client;
    private static final URI uri = URI.create("http://localhost:8080/films");
    private static Gson gson;
    private ConfigurableApplicationContext app;

    @BeforeEach
    void beforeEach() {
        app = SpringApplication.run(FilmorateApplication.class, "");
        client = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    @AfterEach
    void closing() {
        app.close();
    }

    @Test
    void blankNameReturnsCode400() throws IOException, InterruptedException {
        Film film = new Film("", "Good one", LocalDate.of(2000, 1, 1), 100);

        String json = gson.toJson(film);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).setHeader("Content-Type", "application/json").POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void createSuccessReturnsCode200() throws IOException, InterruptedException {
        Film film = new Film("The best", "Good one", LocalDate.of(2000, 1, 1), 100);

        String json = gson.toJson(film);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).setHeader("Content-Type", "application/json").POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void tooLongDescriptionReturnsCode400() throws IOException, InterruptedException {
        Film film = new Film("The best", "Good one, but the only thing I can say about it," +
                "that too mane people who have been watching it for too long got extremely bored, I mean really (!) extremely, Good one, but the only thing I can say about it, " +
                " that too mane people who have been watching it for too long got extremely bored, I mean really (!) extremely",
                LocalDate.of(2000, 1, 1), 100);

        String json = gson.toJson(film);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).setHeader("Content-Type", "application/json").POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void tooEarlyReleaseReturnsCode500() throws IOException, InterruptedException {
        Film film = new Film("The best", "Good one", LocalDate.of(1895, 1, 1), 100);

        String json = gson.toJson(film);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).setHeader("Content-Type", "application/json").POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(500, response.statusCode());
    }

    @Test
    void negDurationReturnsCode400() throws IOException, InterruptedException {
        Film film = new Film("The best", "Good one", LocalDate.of(1954, 1, 1), -5);

        String json = gson.toJson(film);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).setHeader("Content-Type", "application/json").POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void updateSuccessReturnsCode200() throws IOException, InterruptedException {
        Film film = new Film("The best", "Good one", LocalDate.of(1985, 1, 1), 100);
        Film film2 = new Film("The best", "Good one", LocalDate.of(1985, 1, 1), 120);
        film2.setId(1);
        String json = gson.toJson(film);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).setHeader("Content-Type", "application/json").POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String json2 = gson.toJson(film2);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(uri).setHeader("Content-Type", "application/json").PUT(body2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());
    }

    @Test
    void updateFailureReturnsCode500() throws IOException, InterruptedException {
        Film film = new Film("The best", "Good one", LocalDate.of(1985, 1, 1), 100);
        Film film2 = new Film("The best", "Good one", LocalDate.of(1985, 1, 1), 120);
        film2.setId(3);
        String json = gson.toJson(film);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).setHeader("Content-Type", "application/json").POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String json2 = gson.toJson(film2);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(uri).setHeader("Content-Type", "application/json").PUT(body2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(500, response2.statusCode());
    }
}