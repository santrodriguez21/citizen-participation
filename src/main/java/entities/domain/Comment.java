package entities.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Getter
@Setter
public class Comment {
    @Id
    private String id;
    private String userDocument;
    private String description;
    private LocalDate publishDate;
}
