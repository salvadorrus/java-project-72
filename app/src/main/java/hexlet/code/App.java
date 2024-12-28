package hexlet.code;

import hexlet.code.controllers.UrlController;
import hexlet.code.repositiry.BaseRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.stream.Collectors;

@Slf4j
public class App {
    public static Javalin getApp() throws SQLException {

        var hikariConfig = new HikariConfig();
        var urlDataBase = System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;");
        hikariConfig.setJdbcUrl(urlDataBase);
        var dataSource = new HikariDataSource(hikariConfig);

        var url = App.class.getClassLoader().getResourceAsStream("schema.sql");
        assert url != null;
        var sql = new BufferedReader(new InputStreamReader(url))
                .lines().collect(Collectors.joining("\n"));
        log.info(sql);
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        BaseRepository.dataSource = dataSource;

        var app = Javalin.create(config ->
                config.fileRenderer(new JavalinJte(createTemplateEngine())));

        //app.get("/", ctx -> ctx.render("index.jte"));
        //app.get(NamedRoutes.buildUrlPath(), UrlController::build);
        app.get(NamedRoutes.rootPath(), UrlController::root);
        app.get(NamedRoutes.urlsPath(), UrlController::index);
        app.post(NamedRoutes.urlsPath(), UrlController::create);
        app.get(NamedRoutes.urlPath("{id}"), UrlController::show);
        app.post(NamedRoutes.checksPath("{id}"), UrlController::check);

        return app;
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }

    public static void main(String[] args) throws SQLException {
        var app = getApp();
        app.start(getPort());
    }
}
