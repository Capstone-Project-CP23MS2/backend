package sit.cp23ms2.sportconnect.dtos.user_interests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterestDto {
    private Integer userId;
    private String email;
    private Integer categoryId;
    private String categoryName;
}
