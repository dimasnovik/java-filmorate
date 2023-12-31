package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.LocalDateAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    private static HttpClient client;
    private static final URI uri = URI.create("http://localhost:8080/users");
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
    void blankLoginReturnsCode400() throws IOException, InterruptedException {
        User user = new User("test@yandex.ru", "", LocalDate.of(2000, 1, 1));

        String json = gson.toJson(user);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).setHeader("Content-Type", "application/json").POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void createSuccessReturnsCode200() throws IOException, InterruptedException {
        User user = new User("test@yandex.ru", "driver", LocalDate.of(2000, 1, 1));

        String json = gson.toJson(user);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).setHeader("Content-Type", "application/json").POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }


    @Test
    void updateSuccessReturnsCode200() throws IOException, InterruptedException {
        User user = new User("test@yandex.ru", "driver", LocalDate.of(2000, 1, 1));
        User user2 = new User("test@yandex.ru", "driver", LocalDate.of(1996, 1, 1));
        user2.setId(1);
        String json = gson.toJson(user);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).setHeader("Content-Type", "application/json").POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String json2 = gson.toJson(user2);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(uri).setHeader("Content-Type", "application/json").PUT(body2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());
    }

    @Test
    void updateFailureReturnsCode400() throws IOException, InterruptedException {
        User user = new User("test@yandex.ru", "driver", LocalDate.of(2000, 1, 1));
        User user2 = new User("test@yandex.ru", "driver", LocalDate.of(1996, 1, 1));
        user2.setId(3);
        String json = gson.toJson(user);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).setHeader("Content-Type", "application/json").POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String json2 = gson.toJson(user2);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(uri).setHeader("Content-Type", "application/json").PUT(body2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response2.statusCode());
    }

    @Test
    void wrongBirthDayReturnsCode400() throws IOException, InterruptedException {
        User user = new User("test@yandex.ru", "driver", LocalDate.of(2025, 1, 1));

        String json = gson.toJson(user);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).setHeader("Content-Type", "application/json").POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void blankNameChangesToLogin() throws IOException, InterruptedException {
        User user = new User("test@yandex.ru", "driver", LocalDate.of(2000, 1, 1));

        String json = gson.toJson(user);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).setHeader("Content-Type", "application/json").POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        User loadedUser = gson.fromJson(response.body(), User.class);
        assertEquals(user.getLogin(), loadedUser.getName());
    }

    @Test
    void wrongEmailReturnsCode400() throws IOException, InterruptedException {
        User user = new User("@yandex.ru", "driver", LocalDate.of(2000, 1, 1));

        String json = gson.toJson(user);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).setHeader("Content-Type", "application/json").POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    void blankEmailReturnsCode400() throws IOException, InterruptedException {
        User user = new User("", "driver", LocalDate.of(2000, 1, 1));

        String json = gson.toJson(user);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).setHeader("Content-Type", "application/json").POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        assertEquals(400, response.statusCode());
    }

}