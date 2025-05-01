package entities.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@TypeAlias("entities.user.Mayor")
@Getter
@Setter
public class Mayor extends User {
    private String district;
}
