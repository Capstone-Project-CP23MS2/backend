package sit.cp23ms2.sportconnect.dtos.review_user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewUserDto {
    private Integer userId;
    private Integer reviewerId;
    private String comment;
}
