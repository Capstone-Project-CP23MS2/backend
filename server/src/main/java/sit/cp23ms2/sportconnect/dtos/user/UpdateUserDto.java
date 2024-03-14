package sit.cp23ms2.sportconnect.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.cp23ms2.sportconnect.entities.Category;
import sit.cp23ms2.sportconnect.enums.Gender;
import sit.cp23ms2.sportconnect.utils.ValueOfEnum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
    @Size(max = 40, message = "size must be within 40")
    private String username;

    private String profilePicture;

    @Enumerated(EnumType.STRING)
    @ValueOfEnum(enumClass = Gender.class)
    private String gender;

    private String dateOfBirth;

    @Size(max = 10, message = "size must be within 10")
    private String phoneNumber;

    @Size(max = 24, message = "size must be within 24")
    private String lineId;

    private Set<Integer> userInterests;
}
