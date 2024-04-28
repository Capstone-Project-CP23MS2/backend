package sit.cp23ms2.sportconnect.dtos.activity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateActivityDto {

    private Integer categoryId;


    private String title;

    private String description;


    private Integer locationId;
    private Instant dateTime;
    private Integer duration;
    private String lineGroupUrl;
    private Integer noOfMembers;
}
