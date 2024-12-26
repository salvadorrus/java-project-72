package hexlet.code.dto;

import io.javalin.validation.ValidationError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BuildUrlPage extends BasePage{
    private String name;
    private Map<String, List<ValidationError<Object>>> errors;

//    public BuildUrlPage(Map<String, List<ValidationError<Object>>> errors) {
//        this.errors = errors;
//    }
}
