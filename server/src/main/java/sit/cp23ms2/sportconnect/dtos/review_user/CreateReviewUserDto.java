package sit.cp23ms2.sportconnect.dtos.review_user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewUserDto {
    private Integer userId;
    private Integer reviewerId;
    @Size(max = 255, message = "size must not over 255")
    private String comment;
}
