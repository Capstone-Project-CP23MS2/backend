package sit.cp23ms2.sportconnect.dtos.review_activity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ReviewActivityDto {
    private Integer reviewId;
    private Integer activityId;
    private Integer userId;
    private Integer rating;
    private String comment;
    private Instant createdAt;
}
