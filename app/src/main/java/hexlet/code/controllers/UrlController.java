package hexlet.code.controllers;

import hexlet.code.dto.BasePage;
import hexlet.code.model.Url;
import hexlet.code.dto.UrlPage;
import hexlet.code.repositiry.UrlRepository;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static io.javalin.rendering.template.TemplateUtil.model;

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
        page.setFlash(flash);
        ctx.render("urls/index.jte", Collections.singletonMap("page", page));
    }
}
