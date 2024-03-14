package sit.cp23ms2.sportconnect.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.cp23ms2.sportconnect.enums.Gender;
import sit.cp23ms2.sportconnect.enums.Role;
import sit.cp23ms2.sportconnect.utils.ValueOfEnum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {
    private Integer userId;

    @NotNull
    @Size(max = 40, message = "size must be within 40")
    private String username;

    @Size(max = 40, message = "size must be between 1 and 40")
    @Email(message = "must be a well-formed email address")
    @NotBlank(message = "must not be null or blank")
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ValueOfEnum(enumClass = Role.class)
    private String role;

    private String profilePicture;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ValueOfEnum(enumClass = Gender.class)
    private String gender;


    private String dateOfBirth;

    private Integer locationId;

    @NotBlank(message = "must not be null or blank")
    @Size(max = 10, message = "size must be within 10")
    private String phoneNumber;

    @Size(max = 24, message = "size must be within 24")
    private String lineId;

    private Instant lastLogin;
}
