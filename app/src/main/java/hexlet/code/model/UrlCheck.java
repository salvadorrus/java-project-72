package hexlet.code.model;

import hexlet.code.dto.BasePage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor

public class UrlCheck extends BasePage {
    private Long id;
    private int statusCode;
    private String name;
    private String title;
    private String h1;
    private String description;
    private Long urlId;
    private Timestamp createdAt;
    private List<UrlCheck> urlChecks;

    public UrlCheck(int statusCode, String title, String h1, String description, Long urlId) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
    }

    public UrlCheck(Long id, String name, Timestamp createdAt, List<UrlCheck> urlChecks) {

    }
}
