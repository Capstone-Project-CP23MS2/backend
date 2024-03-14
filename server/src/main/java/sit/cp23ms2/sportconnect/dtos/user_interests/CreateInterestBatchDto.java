package sit.cp23ms2.sportconnect.dtos.user_interests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateInterestBatchDto {
    private Integer userId;
    private Set<Integer> categoryIds;
}
