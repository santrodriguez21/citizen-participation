package entities.user;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@TypeAlias("entities.user.Citizen")
public class Citizen extends User {
}
