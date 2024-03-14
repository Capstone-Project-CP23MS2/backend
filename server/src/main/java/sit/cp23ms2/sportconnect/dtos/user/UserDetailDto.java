package sit.cp23ms2.sportconnect.dtos.user;

import lombok.Getter;
import lombok.Setter;
import sit.cp23ms2.sportconnect.entities.Category;
import sit.cp23ms2.sportconnect.entities.Location;
import sit.cp23ms2.sportconnect.enums.Gender;
import sit.cp23ms2.sportconnect.enums.Role;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class UserDetailDto {
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
    private Location location;
    private Set<Category> userInterests;

}
