package sit.cp23ms2.sportconnect.entities.idclass;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserInterestId implements Serializable {
    private Integer user;
    private Integer category;
}
