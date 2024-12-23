package hexlet.code.controllers;

import hexlet.code.dto.BasePage;
import hexlet.code.model.Url;
import hexlet.code.dto.UrlPage;
import hexlet.code.model.UrlCheck;
import hexlet.code.repositiry.UrlCheckRepository;
import hexlet.code.repositiry.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class UrlController {

//    public static void build(Context ctx) {
//        var page = new BuildUrlPage();
//        ctx.render("urls/build.jte", model("page", page));
//    }

    public static void root (Context ctx) {
        var page = new BasePage();
        ctx.render("index.jte", Collections.singletonMap("page", page));
    }

    public static void index(Context ctx) throws SQLException {
        List<Url> urls = UrlRepository.getEntities();
        var page = new UrlPage((Url) urls);
        String flash = ctx.consumeSessionAttribute("flash");
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));
        page.setFlash(flash);
        ctx.render("urls/index.jte", Collections.singletonMap("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Entity with id = " + id + " not found"));
        var urlChecks = UrlCheckRepository.getEntitiesById(id);
        var page = new UrlCheck(id, url.getName(), url.getCreatedAt(), urlChecks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls/show.jte", Collections.singletonMap("page", page));
    }
}

