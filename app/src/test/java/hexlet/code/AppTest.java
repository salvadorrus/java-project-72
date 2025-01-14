package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static hexlet.code.App.readFile;
import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {

    private static Javalin app;
    private static MockWebServer mockServer;

    @BeforeAll
    public static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        MockResponse mockResponse = new MockResponse().setBody(readFile("fixtures/test.html"));
        mockServer.enqueue(mockResponse);
    }

    @BeforeEach
    public final void setUp() throws SQLException {
        app = App.getApp();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        mockServer.shutdown();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }

    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testCreateUrls() throws SQLException {
        var existingUrl = new Url("https://www.example.com");
        UrlRepository.save(existingUrl);

        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.example.com";
            var response = client.post("/urls", requestBody);

            assertThat(response.code()).isEqualTo(200);

            List<Url> savedUrls = UrlRepository.getEntities();
            assertThat(savedUrls)
                    .filteredOn(url -> url.getName().equals("https://www.example.com"))
                    .hasSize(1);
        });
    }

    @Test
    void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/999999");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    void testUrlCheck() throws SQLException {
        var existingUrl = new Url("https://www.example.com");
        UrlRepository.save(existingUrl);

        JavalinTest.test(app, (server, client) -> {
            var requestUrl = "url=https://www.example.com";
            var response = client.post("/urls", requestUrl);

            assertThat(response.code()).isEqualTo(200);

            var afterPost = client.get(NamedRoutes.urlsPath());
            assertThat(afterPost.code()).isEqualTo(200);
        });
    }

    @Test
    void testMockServer() throws SQLException {
        var url = mockServer.url("/").toString();
        Url urlCheck = new Url(url);
        UrlRepository.save(urlCheck);

        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.checksPath(urlCheck.getId()));
            assertThat(response.code()).isEqualTo(200);
            var check = UrlCheckRepository.find(urlCheck.getId()).orElseThrow();

            assertThat(check.getTitle()).isEqualTo("Тест");
            assertThat(check.getH1()).isEqualTo("Анализатор страниц");
            assertThat(check.getDescription()).isEqualTo("");
        });
    }
}

