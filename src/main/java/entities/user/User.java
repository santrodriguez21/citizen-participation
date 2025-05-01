package entities.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "users")
@TypeAlias("user")
public class User {

    private String id;
    private String document;
    private String name;
    private String email;
    private String password;
    private String address;

}
