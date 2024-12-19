package hexlet.code;

import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class UrlController {
    public static void index(Context ctx) throws SQLException {
        List<Url> urls = BaseRepository.getEntities();
        var page = new UrlPage((Url) urls);
        String flash = ctx.consumeSessionAttribute("flash");
        page.setFlash(flash);
        ctx.render("urls/index.jte", Collections.singletonMap("page", page));
    }
}
