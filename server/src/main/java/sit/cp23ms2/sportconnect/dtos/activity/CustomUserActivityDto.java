package sit.cp23ms2.sportconnect.dtos.activity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomUserActivityDto {
    private Integer userId;
    private String username;
    private String email;
}
