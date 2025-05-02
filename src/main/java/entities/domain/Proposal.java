package entities.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Document(collection = "proposals")
public class Proposal {
    @Id
    private String id;
    private String title;
    private String description;
    private String authorDocument;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate limitDate;
    private List<Vote> votes;
    private List<Comment> comments;

}
