package sit.cp23ms2.sportconnect.dtos.user_interests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateInterestDto {
    private Integer userId;
    private Integer categoryId;
}
