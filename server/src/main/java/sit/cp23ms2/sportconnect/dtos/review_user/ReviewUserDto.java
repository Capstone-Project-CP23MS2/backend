package sit.cp23ms2.sportconnect.dtos.review_user;

import lombok.Getter;
import lombok.Setter;
import sit.cp23ms2.sportconnect.entities.User;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ReviewUserDto {
    private Integer reviewId;
    //private Integer userId;
    //private Integer reviewerId;
    private CustomReviewUserDto userReviewed = new CustomReviewUserDto();
    private CustomReviewUserDto reviewer = new CustomReviewUserDto();
    private String comment;
    private Instant createdAt;
}
