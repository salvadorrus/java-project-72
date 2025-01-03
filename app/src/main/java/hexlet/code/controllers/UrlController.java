package hexlet.code.controllers;

import hexlet.code.dto.BasePage;
import hexlet.code.model.Url;
import hexlet.code.dto.UrlPage;
import hexlet.code.model.UrlCheck;
import hexlet.code.repositiry.UrlCheckRepository;
import hexlet.code.repositiry.UrlRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.jsoup.Jsoup;

public class UrlController {

//    public static void build(Context ctx) {
//        var page = new BuildUrlPage();
//        ctx.render("urls/build.jte", model("page", page));
//    }

    public static void root(Context ctx) {
        var page = new BasePage();
        ctx.render("index.jte", Collections.singletonMap("page", page));
    }

    public static void index(Context ctx) throws SQLException {
        List<Url> urls = UrlRepository.getEntities();
        var page = new UrlPage(urls);
        String flash = ctx.consumeSessionAttribute("flash");
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));
        page.setFlash(flash);
        ctx.render("urls/index.jte", Collections.singletonMap("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Integer.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Entity with id = " + id + " not found"));
        var urlChecks = UrlCheckRepository.getEntitiesById(id);
        var page = new UrlCheck(id, url.getName(), url.getCreatedAt(), urlChecks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));
        ctx.render("urls/show.jte", Collections.singletonMap("page", page));
    }

    public static void create(Context ctx) throws SQLException {
        var input = ctx.formParamAsClass("url", String.class)
                .get()
                .toLowerCase()
                .trim();

        String normalizedURL;

        try {
            URL url = new URI(input).toURL();
            normalizedURL = normalizedURL(url);
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flashType", "warning");
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }

        Url url = UrlRepository.findExisting(normalizedURL).orElse(null);

        if (url != null) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flashType", "info");
            ctx.redirect(NamedRoutes.urlsPath());
        } else {
            var newUrl = new Url(normalizedURL);
            UrlRepository.save(newUrl);
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flashType", "success");
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }

    public static void check(Context ctx) throws SQLException {
        int urlId = ctx.pathParamAsClass("id", Integer.class).get();
        var url = UrlRepository.find(urlId).get();
        try {
            HttpResponse<String> response = Unirest.get(url.getName())
                    .asString();
            var doc = Jsoup.parse(response.getBody());
            var statusCode = response.getStatus();
            var title = doc.title();
            var description = doc.select("meta[name=description]").attr("content");
            var h1 = doc.select("h1").text();
            var urlCheck = new UrlCheck(statusCode, title, h1, description, urlId);
            UrlCheckRepository.save(urlCheck);
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flashType", "success");
            ctx.redirect(NamedRoutes.urlPath(urlId));
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("flashType", "danger");
            ctx.redirect(NamedRoutes.urlPath(urlId));
        }
    }

    public static String normalizedURL(URL url) {
        String protocol = url.getProtocol();
        String symbols = "://";
        String host = url.getHost();
        String colonBeforePort = url.getPort() == -1 ? "" : ":";
        String port = url.getPort() == -1 ? "" : String.valueOf(url.getPort());
        return protocol + symbols + host + colonBeforePort + port;
    }
}

