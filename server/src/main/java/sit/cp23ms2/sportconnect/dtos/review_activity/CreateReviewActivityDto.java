package sit.cp23ms2.sportconnect.dtos.review_activity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewActivityDto {
    private Integer activityId;
    private Integer userId;
    private Integer rating;
    private String comment;
}
