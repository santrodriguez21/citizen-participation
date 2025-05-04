package entities.user;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@TypeAlias("entities.user.Moderator")
public class Moderator extends User{
}
