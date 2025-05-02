package entities.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Vote {
    private String userDocument;
    private boolean inFavor;
}
