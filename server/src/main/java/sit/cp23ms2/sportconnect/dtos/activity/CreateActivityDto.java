package sit.cp23ms2.sportconnect.dtos.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateActivityDto {
    private Integer activityId;

    @NotNull(message = "must not be null or blank")
    private Integer hostUserId;
    @NotNull(message = "must not be null or blank")
    private Integer categoryId;

    @NotBlank(message = "must not be null or blank")
    @Size(max = 100, message = "size must not over 100")
    private String title;

    @NotBlank(message = "must not be null or blank")
    private String description;

    private Integer locationId;

    @NotNull(message = "must not be null or blank")
    private Instant dateTime;

    private Integer duration;

    private String lineGroupUrl;

    @NotNull(message = "must not be null or blank")
    private Integer noOfMembers;

    //Auto generated
    private Instant createdAt;
    private Instant updatedAt;
}
