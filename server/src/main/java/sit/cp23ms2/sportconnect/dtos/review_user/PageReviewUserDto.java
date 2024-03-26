package sit.cp23ms2.sportconnect.dtos.review_user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageReviewUserDto {
    private List<ReviewUserDto> content;
    private int number;
    private int size;
    private int totalPages;
    private int numberOfElements;
    private int totalElements;
    private boolean last;
    private boolean first;
}
