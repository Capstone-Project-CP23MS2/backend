package sit.cp23ms2.sportconnect.dtos.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.cp23ms2.sportconnect.enums.NotificationType;
import sit.cp23ms2.sportconnect.utils.ValueOfEnum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNotificationDto {
    private Boolean unRead;

    @Enumerated(EnumType.STRING)
    @ValueOfEnum(enumClass = NotificationType.class)
    private String type;


    private String message;
}
