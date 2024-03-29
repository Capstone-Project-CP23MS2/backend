package sit.cp23ms2.sportconnect.dtos.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import sit.cp23ms2.sportconnect.entities.Activity;
import sit.cp23ms2.sportconnect.entities.Category;
import sit.cp23ms2.sportconnect.entities.Location;
import sit.cp23ms2.sportconnect.enums.Gender;
import sit.cp23ms2.sportconnect.enums.Role;
import lombok.Getter;
import lombok.Setter;


import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Integer userId;
    private String username;
    private String email;
    private Role role;
    private String profilePicture;
    private Gender gender;
    private Date dateOfBirth;
    private String phoneNumber;
    private String lineId;
    private Instant lastLogin;
    private LocalDate registrationDate;
//    private Location location;
//    private Set<Category> userInterests;

//    private Gender convertGender(Gender gender) {
//        Gender.fromEnum()
//    }

    private Set<Category> userInterests;

    @JsonIgnore
    private Set<Activity> userActivities = new HashSet<>();
}
