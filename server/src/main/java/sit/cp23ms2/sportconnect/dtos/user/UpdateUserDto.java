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

    private String username;

    private String profilePicture;

    @Enumerated(EnumType.STRING)
    @ValueOfEnum(enumClass = Gender.class)
    private String gender;

    private String dateOfBirth;


    private String phoneNumber;


    private String lineId;

    private Set<Integer> userInterests;
}
