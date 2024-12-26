package hexlet.code.controllers;

import hexlet.code.dto.BasePage;
import hexlet.code.dto.BuildUrlPage;
import hexlet.code.model.Url;
import hexlet.code.dto.UrlPage;
import hexlet.code.model.UrlCheck;
import hexlet.code.repositiry.UrlCheckRepository;
import hexlet.code.repositiry.UrlRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.validation.ValidationException;
import org.apache.commons.validator.routines.UrlValidator;

import java.net.URI;
import java.net.URISyntaxException;
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

    public static void create(Context ctx) throws SQLException, URISyntaxException {
        var beginnerUrl = ctx.formParam("url");
        String nameUrl = null;
        try {
            assert beginnerUrl != null;
            var uri = new URI(beginnerUrl);
            nameUrl = uri.getScheme() + "://" + uri.getAuthority();
        } catch (ValidationException e) {
            var page = new BuildUrlPage(beginnerUrl, e.getErrors());
            ctx.status(422).render("index.jte", Collections.singletonMap("page", page));
        }

        if (!validateUrl(nameUrl)) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flashType", "danger");
            var page = new BuildUrlPage();
            page.setFlash(ctx.consumeSessionAttribute("flash"));
            page.setFlashType(ctx.consumeSessionAttribute("flashType"));
            ctx.render("index.jte", Collections.singletonMap("page", page));
            return;
        }
        if (UrlRepository.findExisting(nameUrl)) {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flashType", "info");
            ctx.redirect(NamedRoutes.urlsPath());
        } else {
            Url resultUrl = new Url(nameUrl);
            UrlRepository.save(resultUrl);
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flashType", "success");
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }

    public static boolean validateUrl(String url) {
        String[] schemas = {"http", "https"};
        UrlValidator validator = new UrlValidator(schemas, UrlValidator.ALLOW_LOCAL_URLS);
        return validator.isValid(url);
    }
}

