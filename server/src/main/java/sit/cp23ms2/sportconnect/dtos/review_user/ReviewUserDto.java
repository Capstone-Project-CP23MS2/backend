package sit.cp23ms2.sportconnect.dtos.review_user;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ReviewUserDto {
    private Integer reviewId;
    private Integer userId;
    private Integer reviewerId;
    private String comment;
    private Instant createdAt;
}
