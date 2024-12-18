package hexlet.code;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    public static Javalin getApp() {

        var hikariConfig = new HikariConfig();
        var url = System.getenv().getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;");
        hikariConfig.setJdbcUrl(url);
        BaseRepository.dataSource = new HikariDataSource(hikariConfig);

        var app = Javalin.create(config ->
                config.fileRenderer(new JavalinJte(createTemplateEngine())));

        app.get("/", ctx -> ctx.render("index.jte"));
        return app;
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(7070);
    }
}
