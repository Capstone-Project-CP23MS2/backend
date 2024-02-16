package sit.cp23ms2.sportconnect.entities;

import lombok.Getter;
import lombok.Setter;
import sit.cp23ms2.sportconnect.entities.idclass.RequestId;
import sit.cp23ms2.sportconnect.entities.idclass.UserInterestId;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "userInterests")
@IdClass(UserInterestId.class)
public class UserInterest implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;
}
