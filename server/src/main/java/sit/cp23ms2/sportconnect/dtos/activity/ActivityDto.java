package sit.cp23ms2.sportconnect.dtos.activity;

import sit.cp23ms2.sportconnect.entities.Category;
import sit.cp23ms2.sportconnect.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;


import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ActivityDto {
    private Integer activityId;
    private Integer hostUserId;
    //private Category categoryId;
    private Integer categoryId;
    private String categoryName;
    private String title;
    private String description;
    private String place;
    private Instant dateTime;
    private Integer duration;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer noOfMembers;

    //custom only 3 fields
    private Set<CustomUserActivityDto> users = new HashSet<>();

}
