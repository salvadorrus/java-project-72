package hexlet.code.model;

import hexlet.code.dto.BasePage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor

public class UrlCheck {
    private int id;
    private int statusCode;
    private String name;
    private String title;
    private String h1;
    private String description;
    private int urlId;
    private Timestamp createdAt;
    //private List<UrlCheck> urlChecks;

    public UrlCheck(int statusCode, String title, String h1, String description, int urlId) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
    }

//    public UrlCheck(int urlId, int statusCode, String title, String h1, String description, LocalDateTime createdAt) {
//
//    }

//    public UrlCheck(int id, String name, Timestamp createdAt, List<UrlCheck> urlChecks) {
//
//    }
}
